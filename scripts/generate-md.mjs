// README.md（英語）から、画像とリンクを絶対URLに変換した README-platform.md を生成する。
// Modrinth / CurseForge の説明欄では相対パスが解決できないため、そこへ貼り付ける用。
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
const OUT = path.join(root, "README-platform.md");

const isAbsolute = (url) => /^(https?:)?\/\//.test(url) || url.startsWith("#") || url.startsWith("mailto:");

let md = readFileSync(SRC, "utf8");

// 1) HTML の <img src="..."> （相対 → raw 画像URL）
md = md.replace(/src="([^"]+)"/g, (m, url) => (isAbsolute(url) ? m : `src="${RAW}/${url}"`));

// 2) Markdown 画像 ![alt](path) （相対 → raw 画像URL）
md = md.replace(/!\[([^\]]*)\]\(([^)]+)\)/g, (m, alt, url) => (isAbsolute(url) ? m : `![${alt}](${RAW}/${url})`));

// 3) Markdown リンク [text](path) （画像でない・相対 → blob リポジトリURL）
md = md.replace(/(?<!!)\[([^\]]+)\]\(([^)]+)\)/g, (m, text, url) => (isAbsolute(url) ? m : `[${text}](${BLOB}/${url})`));

const header =
  "<!-- AUTO-GENERATED from README.md by scripts/generate-md.mjs — do not edit directly. Run: pnpm generate-md -->\n\n";
writeFileSync(OUT, header + md, "utf8");

console.log(`Generated ${path.relative(root, OUT)} (branch: ${BRANCH})`);
