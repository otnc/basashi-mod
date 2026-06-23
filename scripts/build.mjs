// 各MCバージョン(versions/<mc>/ の独立ビルド)をビルドし、dist/ に jar を集約する。
//   pnpm build -v 1.1.0                … 全バージョンをビルド
//   pnpm build --mc 1.16.5 -v 1.1.0    … 指定バージョンのみ
//   -v 省略時は dev
import { execSync } from "child_process";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, "..");

// バージョンごとの設定（ビルドツールチェーンが異なるため独立プロジェクト）
const VERSIONS = {
  "1.20.1": {
    dir: "versions/1.20.1",
    javaTool: "temurin-17",
    jar: (mod) => `forge/build/libs/basashi-forge-1.20.1-${mod}.jar`, // Architectury(common+forge)
  },
  "1.16.5": {
    dir: "versions/1.16.5",
    javaTool: "temurin-8",
    jar: (mod) => `build/libs/basashi-1.16.5-${mod}.jar`, // 素Forge(単一)
  },
};

function arg(names) {
  const a = process.argv.slice(2);
  for (let i = 0; i < a.length; i++) {
    if (names.includes(a[i])) return a[i + 1];
    for (const n of names) if (a[i].startsWith(n + "=")) return a[i].slice(n.length + 1);
  }
  return null;
}

const mcArg = arg(["--mc"]);
const mod = arg(["-v", "--version"]) || "dev";
const targets = mcArg ? [mcArg] : Object.keys(VERSIONS);

function resolveJava(tool) {
  try {
    return execSync(`mise where java@${tool}`, { encoding: "utf8" }).trim();
  } catch {
    return process.env.JAVA_HOME || null;
  }
}

const distDir = path.join(root, "dist");
fs.mkdirSync(distDir, { recursive: true });
const built = [];

for (const mc of targets) {
  const cfg = VERSIONS[mc];
  if (!cfg) {
    console.error(`✗ 未知のMCバージョン: ${mc}（対応: ${Object.keys(VERSIONS).join(", ")}）`);
    process.exit(1);
  }
  const vdir = path.join(root, cfg.dir);
  const javaHome = resolveJava(cfg.javaTool);
  const env = { ...process.env };
  if (javaHome) {
    env.JAVA_HOME = javaHome;
    env.PATH = path.join(javaHome, "bin") + path.delimiter + env.PATH;
  }
  const gradlew = path.join(vdir, process.platform === "win32" ? "gradlew.bat" : "gradlew");
  console.log(`\n=== ${mc} : basashi ${mod} をビルド (java=${cfg.javaTool}${javaHome ? "" : " ※未解決"}) ===`);
  execSync(`"${gradlew}" build -Pmod_version=${mod}`, { cwd: vdir, env, stdio: "inherit" });

  const src = path.join(vdir, cfg.jar(mod));
  if (!fs.existsSync(src)) {
    console.error(`✗ jar が見つかりません: ${src}`);
    process.exit(1);
  }
  const dest = path.join(distDir, `basashi-${mc}-${mod}.jar`);
  fs.copyFileSync(src, dest);
  built.push(path.relative(root, dest));
}

console.log("\n✅ ビルド完了。dist/ に出力:");
for (const b of built) console.log(`   ${b}`);
console.log("これらを各MCバージョンの Forge/NeoForge の mods に入れてテストできます。");
