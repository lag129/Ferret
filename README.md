# Ferret

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/lag129/Ferret/blob/main/LICENSE)
[![Android API](https://img.shields.io/badge/API-30%2B-brightgreen.svg?style=flat)](https://developer.android.com/tools/releases/platforms?hl=ja#11)
[![Build Status](https://github.com/lag129/Ferret/workflows/Android%20CI/badge.svg)](https://github.com/lag129/Ferret/actions)

Ferret は Android 向けの軽量な [Mastodon](https://joinmastodon.org/)
クライアントアプリです。Mastodon
は分散型のSNSサービスであり、Ferret
はそのクライアントアプリとして動作します。一部の機能では [Misskey](https://misskey-hub.net/)
の操作にも対応しています。

絵文字やリアクションの表示が行えるシンプルな見た目のアプリを目指して開発しています。

## 実装機能

- タイムラインの表示 (Mastodon / Misskey)
- プロフィールの表示 (Mastodon / Misskey)
- 絵文字, リアクションの表示 (Mastodon)
- 検索機能 (Mastodon)

実装機能の詳細や動作画面については[機能概要](/docs/機能概要.md)に記述しています。

## 使用技術

| 分類           | 技術                        |
|:---------------|-----------------------------|
| 言語           | Kotlin                      |
| UI             | Jetpack Compose, Material 3 |
| 画面遷移       | Navigation 3                |
| DI             | Koin                        |
| HTTP 通信      | Ktor                        |
| データ永続化   | Room, DataStore             |
| 画像表示       | Coil 3                      |
| HTML 解析      | Ksoup                       |
| パフォーマンス | Baseline Profile            |
| テスト         | Kotest, MockK               |

## ディレクトリ構成

```
.
├── app/
│   ├── schemas/
│   └── src/
│       ├── main/java/net/lag129/ferret/
│       │   ├── db/                 # Entity, DAO
│       │   ├── model/
│       │   │   ├── converter/
│       │   │   ├── mastodon/       # Mastodon API モデル
│       │   │   └── misskey/        # Misskey API モデル
│       │   ├── repository/         # リポジトリ
│       │   ├── ui/
│       │   │   ├── compose/        # コンポーザブル
│       │   │   ├── navigation/     # Navigation Display
│       │   │   ├── screen/         # 各UI画面
│       │   │   └── theme/
│       │   └── utils/              # HTML パーサ, Emoji パーサなど
│       ├── test/
│       └── androidTest/
└── baselineprofile/
```

## アーキテクチャ概要図

![architecture-overview.png](docs/assets/architecture-overview.png)

## ライセンス

[MIT license](https://github.com/lag129/Ferret/blob/main/LICENSE)
