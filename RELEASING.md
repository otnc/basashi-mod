# リリース運用ガイド

馬刺しMOD のバージョン管理・リリース方法と、複数 Minecraft バージョン展開の方針をまとめる。

---

## バージョン形式

| 種類 | 形式 | 例 | 定義場所 |
|------|------|----|----------|
| MODバージョン | SemVer `MAJOR.MINOR.PATCH` | `1.1.0` | リリース時に入力（`-Pmod_version`） |
| 対象MCバージョン | `x.y.z` | `1.20.1` | 各 `versions/<mc>/gradle.properties` |
| 配布jar名 | `basashi-<MC>-<MOD>.jar` | `basashi-1.16.5-1.1.0.jar` | Release 時に生成 |
| 公開バージョン（Modrinth/CF） | `<MOD>+<MC>` | `1.1.0+1.16.5` | release.yml で合成 |

- **MODバージョンはMCと独立**。機能追加=MINOR、バグ修正=PATCH、互換を壊す変更=MAJOR。
- MODバージョンは `gradle.properties` にハードコードせず、ビルド時に渡す（未指定なら `dev`）。
- jar名にMCバージョンを含めることで、どのMC向けかが一目で分かる。

### SemVer の上げ方の目安
- `PATCH`（1.0.0→1.0.1）: バグ修正のみ
- `MINOR`（1.0.0→1.1.0）: アイテム/レシピ追加など後方互換のある機能追加
- `MAJOR`（1.0.0→2.0.0）: 既存の挙動・ID変更などワールドに影響する非互換変更（ID変更は `MissingMappings` で旧ID→新IDへリマップして互換維持）

---

## 複数 Minecraft バージョンの展開方針

**モノレポ**。各MC版を `versions/<mc>/` 以下の独立 Gradle プロジェクトとして同一ブランチに持つ（ブランチを分けない）。

| ディレクトリ | 対象MC | ローダー | 前提MOD |
|---|---|---|---|
| `versions/1.20.1` | 1.20.1 | Forge / NeoForge | Architectury API |
| `versions/1.16.5` | 1.16.5 | Forge | なし |
| `versions/1.12.2` | 1.12.2 | Forge | なし |

- 新しいMC版を足すときは `versions/<mc>/` を追加し、`scripts/build.mjs` と CI（build.yml / release.yml）のマトリクスに1行加える。
- 1リリースで**全版を同時にビルド・公開**する。

---

## リリース手順

リリースは **GitHub Actions の手動実行（バージョン入力）** で行う。タグやファイル更新は不要。

### Release ワークフローを実行
GitHub の **Actions → Release → Run workflow**:
- **Use workflow from**: リリース対象のブランチを選択（通常 `main`）
- **MODバージョン**: リリースする版を入力（例 `1.1.0`）

→ [`.github/workflows/release.yml`](.github/workflows/release.yml) が自動で:
- 全MC版を入力したMODバージョンでビルド（`-Pmod_version=<入力値>`）
- 各版を Modrinth / CurseForge へ個別バージョン `<MOD>+<MC>`（例 `1.1.0+1.16.5`）として公開
- 全版の `basashi-<MC>-<MOD>.jar` を **1つの GitHub Release**（タグ `v<MOD>`）に添付

> 各MC版を独立した公開バージョンにするため、ローダー・対応MC・前提MOD（Architectury は 1.20.1 のみ）を版ごとに正しく設定できる。

---

## Modrinth / CurseForge への公開

`release.yml` は [`Kir-Antipov/mc-publish`](https://github.com/Kir-Antipov/mc-publish) で各MC版を Modrinth / CurseForge へ公開する。**プロジェクトは各サイトで事前に手動作成**しておくこと（初回のみ）。

GitHub リポジトリの Settings に以下を登録すると有効化される（未設定のプラットフォームは自動スキップ）:

| 種類 | 場所 | 名前 | 内容 |
|------|------|------|------|
| Variable | Settings → Secrets and variables → Actions → **Variables** | `MODRINTH_PROJECT_ID` | Modrinth のプロジェクトID（slug可） |
| Variable | 同上 | `CURSEFORGE_PROJECT_ID` | CurseForge の数値プロジェクトID |
| Secret | 同 → **Secrets** | `MODRINTH_TOKEN` | Modrinth の API トークン（[PAT発行](https://modrinth.com/settings/pats)） |
| Secret | 同上 | `CURSEFORGE_TOKEN` | CurseForge の API トークン |

- ID（Variable）とトークン（Secret）の**両方**が揃ったプラットフォームのみ公開される。
- 公開メタ情報（ローダー・ゲームバージョン・前提MOD）は `release.yml` のマトリクスで版ごとに指定済み。
- 各サイトで初回ファイルは審査が入る場合がある。

---

## CI（ビルド検証）

[`.github/workflows/build.yml`](.github/workflows/build.yml) は **手動実行のみ**（Actions タブ → Build → Run workflow）。push では自動で走らない。実行すると全MC版をマトリクスでビルド検証し、各版の jar を Actions の Artifacts から14日間ダウンロードできる。リリース時のビルドは [`release.yml`](.github/workflows/release.yml) が別途行う。

---

## チェックリスト（リリース前）

- [ ] 入力する `mod_version` が正しいか（SemVer）
- [ ] `pnpm build -v <MOD>` がローカルで全版通るか
- [ ] 各MC版でゲーム内動作確認したか（ドロップ・焼き・クラフト・取引）
- [ ] 新規アイテムのID・翻訳・モデル・テクスチャが全版そろっているか
