# AGENTS.md — エージェント開発ガイド

馬刺しMOD（Basashi Mod）の開発を支援する AI エージェント向けの取り決め。

## プロジェクト概要

- Minecraft **1.20.1** の食料追加MOD。**Forge / NeoForge** 対応（1.20.1 は Forge 互換 jar 1つで両対応）。
- **Architectury**（`common` + `forge`）。Java 17 / Gradle。
- 詳細仕様 → [DESIGN.md](./DESIGN.md) / 開発手順 → [CONTRIBUTING.md](./CONTRIBUTING.md) / リリース運用 → [RELEASING.md](./RELEASING.md)。

## 遵守事項（重要）

- **ビルドはエージェントが実行しない**（`pnpm build` / `gradlew build` / `runClient` など）。コマンドの提示までにとどめ、**実行は人間**が行う。
- **トークンを大量に消費する恐れのある操作は事前に相談**する。
- **外部公開を伴う操作**（GitHub への push、Modrinth / CurseForge への API 更新、リリース）は、内容を提示して合意を得てから行う。
- **コミット / プッシュはユーザーの明示の指示があったときのみ**。

## ブランチ運用

- ブランチ名は **`[status]/[title]`** 形式にする。
  - `status`: `feature` / `fix` / `docs` / `release` など
  - `title`: 内容を表す名前（版数は使わない）。例: `feature/new-foods-and-enchant`, `fix/horse-drop`, `docs/readme`
- `main` が安定版（現在 MC 1.20.1）。別の MC バージョンに対応する場合はブランチを分ける。

## バージョン

- `gradle.properties` に `mod_version` を**ハードコードしない**（未指定時は `dev`）。
- ビルド時に指定: `pnpm build -v x.y.z` / `gradlew build -Pmod_version=x.y.z`。
- リリースは GitHub Actions の **Release** ワークフローで版数を入力。
- **後方互換を保てるなら MINOR**（例: 1.1.0）。アイテムID変更は forge の `MissingMappingsEvent` で旧ID→新IDへリマップして互換維持する。

## ドキュメント / ディレクトリ

- `changelog/vX.Y.Z.md` … 公開用の変更履歴。
- `.private/` … 非公開の計画書（**gitignore 済み**、コミットしない）。
- `docs/img/` … README 用画像。`pnpm items` で `items.png`（6個/行で折返し）を再生成。
- `versions/<mc>/` … 各MC版の独立 Gradle プロジェクト（`1.20.1` は Architectury の `common`+`forge`、`1.16.5`/`1.12.2` は素 Forge、`1.7.10` は素 Forge / RetroFuturaGradle）。`scripts/build.mjs` が全版をビルドし `dist/` へ集約。

## コード / アセット方針

- パッケージ管理は **pnpm**（バージョンは固定しない）。整形は Prettier（`pnpm format`）。
- テクスチャは **16×16**、アイテムモデルは `item/generated`。
- 食料の隠し満腹度 = `nutrition × saturationModifier × 2`。
- エディタは VS Code 推奨、ターミナルは PowerShell 7 (pwsh) 推奨。
