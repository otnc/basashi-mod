# 馬刺しMOD (Basashi Mod)

<img src="icon.png" alt="馬刺しMOD アイコン" width="120" align="right">

馬を倒して馬刺しを集め、焼き・調理で馬肉料理を楽しむ食料追加MODです（Minecraft **1.20.1**）。

**対応ローダー: Forge / NeoForge（1.20.1）**

---

## アイテム

![アイテム一覧](docs/img/items.png)

| アイテム | 入手方法 | 空腹度 | 隠し満腹度 |
|----------|----------|:---:|:---:|
| 馬刺し | 馬を倒す | 🍖×3 | 1.8 |
| 馬のタタキ | 馬刺しを焼く／燃えている馬を倒す | 🍖×8 | 12.8 |
| 馬肉のユッケ | 馬刺し＋卵でクラフト | 🍖×6 | 7.2 |
| 馬肉のタルタルステーキ | 馬刺し＋ニンジン＋ビートルート＋卵でクラフト | 🍖×11 | 19.8 |

> 「空腹度」は回復する空腹ゲージ（半分=0.5）、「隠し満腹度」は空腹度の減りにくさに影響する隠しパラメータです。

## 入手・調理

- 🐴 **馬を倒す** → 馬刺し がドロップ
- 🔥 **燃えている馬を倒す** → 馬のタタキ が直接ドロップ
- 🍳 **馬刺しを焼く**（かまど／燻製機／焚火）→ 馬のタタキ

## レシピ

### 馬のタタキ（かまど・燻製機・焚火）
![タタキのレシピ](docs/img/recipe_tataki.png)

### 馬肉のユッケ（馬刺し＋卵）
![ユッケのレシピ](docs/img/recipe_yukke.png)

### 馬肉のタルタルステーキ（馬刺し＋ニンジン＋ビートルート＋卵）
![タルタルステーキのレシピ](docs/img/recipe_tartare.png)

> クラフトは並べ方自由（shapeless）です。

## ダウンロード

以下のいずれからでも入手できます（中身は同じ jar です）:

- [GitHub Releases](https://github.com/otnc/basashi-mod/releases)
- [Modrinth](https://modrinth.com/mod/basashi-mod) **(審査中)**
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/basashi-mod) **(審査中)**

> Modrinth / CurseForge は公開審査中のため、ページが表示されない場合があります。その間は GitHub Releases をご利用ください。

## 導入方法

1. **Forge 1.20.1** または **NeoForge 1.20.1** を導入する
2. 前提MOD **[Architectury API](https://www.curseforge.com/minecraft/mc-mods/architectury-api)**（1.20.1 / Forge版）を `mods` フォルダに入れる
3. 上記ダウンロード先から jar ファイルを入手し、`mods` フォルダに入れる
4. ゲームを起動する

> 1.20.1 では NeoForge が Forge と互換のため、この jar 1つで両ローダーに対応します。

## ライセンス

[MIT License](LICENSE) © otoneko.

## 開発者向け

ビルド方法・開発環境・リリース手順は [CONTRIBUTING.md](CONTRIBUTING.md) を参照してください。仕様の詳細は [DESIGN.md](DESIGN.md) にあります。
