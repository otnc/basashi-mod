// README.md（英語）から、各配布プラットフォーム向けの説明文を生成する。
// 相対パスを絶対URLに変換（プラットフォームの説明欄では相対パスが解決できないため）。
//  - README-modrinth.md   … Modrinth 用（<details> をそのまま使用）
//  - README-curseforge.md … CurseForge 用（<details>/<summary> は非対応なので展開）
// 使い方: pnpm generate-md
import { readFileSync, writeFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const REPO = "otnc/basashi-mod";
const BRANCH = "main"; // 公開時に参照されるブランチ（リリースは main 想定。変えたい場合はここ）
const RAW = `https://raw.githubusercontent.com/${REPO}/${BRANCH}`;
const BLOB = `https://github.com/${REPO}/blob/${BRANCH}`;

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const SRC = path.join(root, "README.md");

const isAbsolute = (url) => /^(https?:)?\/\//.test(url) || url.startsWith("#") || url.startsWith("mailto:");

// 相対パス → 絶対URL（画像は raw、リポジトリ内リンクは blob）
function toAbsolute(md) {
  return md
    .replace(/src="([^"]+)"/g, (m, url) => (isAbsolute(url) ? m : `src="${RAW}/${url}"`))
    .replace(/!\[([^\]]*)\]\(([^)]+)\)/g, (m, alt, url) => (isAbsolute(url) ? m : `![${alt}](${RAW}/${url})`))
    .replace(/(?<!!)\[([^\]]+)\]\(([^)]+)\)/g, (m, text, url) => (isAbsolute(url) ? m : `[${text}](${BLOB}/${url})`));
}

// <details>/<summary> を展開（CurseForge は非対応）。summary は太字の見出しに変換。
function flattenDetails(md) {
  return md
    .replace(/^[ \t]*<\/?details>[ \t]*\r?\n?/gm, "")
    .replace(/<summary>([\s\S]*?)<\/summary>/g, (m, inner) => `**${inner.replace(/<\/?[^>]+>/g, "").trim()}**`);
}

const header =
  "<!-- AUTO-GENERATED from README.md by scripts/generate-md.mjs — do not edit directly. Run: pnpm generate-md -->\n\n";

const base = toAbsolute(readFileSync(SRC, "utf8"));

const outputs = {
  "README-modrinth.md": header + base,
  "README-curseforge.md": header + flattenDetails(base),
};

for (const [name, content] of Object.entries(outputs)) {
  writeFileSync(path.join(root, name), content, "utf8");
  console.log(`Generated ${name}`);
}
console.log(`(branch: ${BRANCH})`);
