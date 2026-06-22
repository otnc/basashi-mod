// ローカルでMOD jarをビルドし、分かりやすい場所 (dist/) に出力する。
//   pnpm build                 … gradle.properties の mod_version を使用
//   pnpm build -v 1.1.0        … バージョンを指定（--version でも可）
//   pnpm build --version 1.1.0
// （pnpm が -v を取り込む場合は `pnpm build -- -v 1.1.0` のように -- を挟む）
import { execSync } from "child_process";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, "..");

function prop(key) {
  const txt = fs.readFileSync(path.join(root, "gradle.properties"), "utf8");
  const m = txt.match(new RegExp("^" + key + "=(.*)$", "m"));
  return m ? m[1].trim() : null;
}

// -v / --version でMODバージョンを指定（無ければ gradle.properties の既定値）
function parseVersionArg(argv) {
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i];
    if (a === "--version" || a === "-v") return argv[i + 1];
    if (a.startsWith("--version=")) return a.slice("--version=".length);
    if (a.startsWith("-v=")) return a.slice("-v=".length);
  }
  return null;
}

const mc = prop("minecraft_version");
const cliVer = parseVersionArg(process.argv.slice(2));
// -v 未指定なら gradle.properties の mod_version、それも無ければ "dev"
const mod = cliVer || prop("mod_version") || "dev";

// JAVA_HOME を解決（未設定なら mise から取得）
let javaHome = process.env.JAVA_HOME;
if (!javaHome) {
  try {
    javaHome = execSync("mise where java@temurin-17", { encoding: "utf8" }).trim();
  } catch {
    /* mise が無ければ無視 */
  }
}
const env = { ...process.env };
if (javaHome) {
  env.JAVA_HOME = javaHome;
  env.PATH = path.join(javaHome, "bin") + path.delimiter + env.PATH;
}

const gradlew = path.join(root, process.platform === "win32" ? "gradlew.bat" : "gradlew");
console.log(
  `Building basashi ${mod} for Minecraft ${mc}  ` +
    `[${cliVer ? "-v 指定" : "gradle.properties"}]${javaHome ? `  (JAVA_HOME=${javaHome})` : ""}`
);
execSync(`"${gradlew}" build -Pmod_version=${mod}`, { cwd: root, env, stdio: "inherit" });

const src = path.join(root, "forge/build/libs", `basashi-forge-${mc}-${mod}.jar`);
if (!fs.existsSync(src)) {
  console.error(`\n✗ Jar not found: ${src}`);
  process.exit(1);
}

const distDir = path.join(root, "dist");
fs.mkdirSync(distDir, { recursive: true });
const dest = path.join(distDir, `basashi-${mc}-${mod}.jar`);
fs.copyFileSync(src, dest);

console.log("\n✅ ビルド完了");
console.log(`   出力: ${path.relative(root, dest)}  (Forge / NeoForge ${mc} 用)`);
console.log("   このjarを CurseForge / Forge の mods に入れてテストできます。");
