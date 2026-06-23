// アイテム一覧画像 (docs/img/items.png) を生成する。
// テクスチャを拡大して 6 個/行 で並べ、7 個目以降は次の行へ折り返す。
//   実行: npm run items
import { PNG } from "pngjs";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, "..");
// テクスチャは 1.20.1 を基準に使用（各版で共通の絵柄）
const texDir = path.join(root, "versions/1.20.1/common/src/main/resources/assets/basashi/textures/item");
const outFile = path.join(root, "docs/img/items.png");

// 並び順
const items = [
  "basashi",
  "horse_tataki",
  "horse_yukke",
  "horse_tartare",
  "horse_hamburg",
  "horse_hamburg_deluxe",
  "horse_liver",
  "cooked_horse_liver",
  "golden_wheat",
  "golden_bread",
];

const SCALE = 8; // 16px -> 128px
const GAP = 16;
const PAD = 12;
const PER_ROW = 6;
const BG = [240, 240, 240, 255];

const sprites = items.map((id) => PNG.sync.read(fs.readFileSync(path.join(texDir, id + ".png"))));
const cellOf = (sp) => sp.width * SCALE; // 通常 16*8=128

const cols = Math.min(PER_ROW, items.length);
const rows = Math.ceil(items.length / PER_ROW);
const cell = 16 * SCALE;
const W = PAD * 2 + cols * cell + (cols - 1) * GAP;
const H = PAD * 2 + rows * cell + (rows - 1) * GAP;

const out = new PNG({ width: W, height: H });
for (let i = 0; i < out.data.length; i += 4) {
  out.data[i] = BG[0];
  out.data[i + 1] = BG[1];
  out.data[i + 2] = BG[2];
  out.data[i + 3] = BG[3];
}

items.forEach((id, idx) => {
  const sp = sprites[idx];
  const scale = cell / sp.width;
  const col = idx % PER_ROW;
  const row = Math.floor(idx / PER_ROW);
  const ox = PAD + col * (cell + GAP);
  const oy = PAD + row * (cell + GAP);
  for (let y = 0; y < cell; y++) {
    for (let x = 0; x < cell; x++) {
      const sx = Math.floor(x / scale);
      const sy = Math.floor(y / scale);
      const si = (sy * sp.width + sx) * 4;
      const a = sp.data[si + 3];
      if (a === 0) continue;
      const di = ((oy + y) * W + (ox + x)) * 4;
      const af = a / 255;
      out.data[di] = Math.round(sp.data[si] * af + out.data[di] * (1 - af));
      out.data[di + 1] = Math.round(sp.data[si + 1] * af + out.data[di + 1] * (1 - af));
      out.data[di + 2] = Math.round(sp.data[si + 2] * af + out.data[di + 2] * (1 - af));
      out.data[di + 3] = 255;
    }
  }
});

fs.mkdirSync(path.dirname(outFile), { recursive: true });
fs.writeFileSync(outFile, PNG.sync.write(out));
console.log(`Generated ${path.relative(root, outFile)} (${W}x${H}, ${items.length} items, ${PER_ROW}/row)`);
