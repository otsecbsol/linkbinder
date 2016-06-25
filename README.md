# Link Binder
[![Apache License](http://img.shields.io/badge/license-Apache-blue.svg?style=flat)](LICENSE)

![Link Binder](logo.png)
---
必要な情報コンテンツはすぐに見つけられますか？十分に活用できていますか？
文書の回覧・承認・再利用のサイクルは回っていますか？
重要な文書があちこちに散在し、企業の情報資産が個人管理任せになっていませんか？

Link Binderは、効率的な文書管理を実現するためのオープンソース・ソフトウェアです。

## 特徴

Link Binderは、基本機能だけでも次の機能を実現しています。

1. プロジェクト単位の文書管理 (ユーザー権限設定、アクセス制限など)
* ワープロソフトのような文書レイアウトを作り、電子メールのようにシンプルな送受信
* 文書の分類ごとに回覧・承認ルートを設定し、漏れのない情報共有
* 添付ファイルを含めた高度な全文検索機能
* 電子メールによる通知やスマートフォン対応により社外からもアクセス

## 前提

次のソフトウェアに依存しています。

* Java8
* Oracle 11g
  * Express Edition(無償版)で動作確認済です
* [Redis](http://redis.io/)
* [Elasticsearch](https://www.elastic.co/jp/) (全文検索機能を利用する場合は必須)

## 使い方

Link Binderは、全文検索インデックス操作やメール通知を行うための`Subscriber`と、Webブラウザ上でユーザーインターフェイスを提供する`Web`の2つのアプリケーションで構成されています。

デモ的な動作であれば、後述するインストール手順を実施後に以下コマンドを実行すると、Link Binderを起動することができます。

### Subscriberの起動

```
./run_subscriber.sh
```

### Webの起動

```
./run_web.sh
```

起動後、ブラウザで http://localhost:8080/ を表示し次のID/パスワードでログインしてください。

* 00000 / password

また、 http://localhost:8080/setup.jsf から、初期データのインポートを行うことができます。

## インストール

Dockerを実行可能な環境で以下を実行し、コンテナを作成します。

```
./setup.sh
```
データベースのセットアップを行います。
```
# dockerのDBコンテナに入る
docker exec -it linkbinder_db bash
# データベースを作成する
root@xxxx:/# sqlplus sys/oracle as sysdba @/root/ddl/create_tablespace.sql
root@xxxx:/# sqlplus sys/oracle as sysdba @/root/ddl/create_user.sql
root@xxxx:/# exit
```
```
./gradlew update -PrunList=production
```
アプリケーションをビルドします。
```
./build.sh
```

## License

[Apache License](https://github.com/otsecbsol/linkbinder/blob/master/LICENSE)

## Author

[Open Tone 業務ソリューション事業部](https://github.com/otsecbsol)
