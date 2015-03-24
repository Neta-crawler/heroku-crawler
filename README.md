# Neta-crawler

WEBサイトをクロールして、記事を集めるロジックです。

### How to develop at local?

* cloneして、`gradle eclipse`
* Eclipseにインポート

### How to run at local?

* サイトは、`neta.crawler.Main`を起動して、`localhost:8080`へアクセス
* クロール処理は、`neta.cralwer.Batch`を起動！

### How to develop crawler?

* `neta.crawler.process.Crawler`インターフェースを実装したクラスを作る
* `neta.crawler.process.Crawlers`の定義に作成したクラスを追加する
* 作成したクラスで、Jsoup使って頑張る

### How to deploy to Heroku?

comming soon!
