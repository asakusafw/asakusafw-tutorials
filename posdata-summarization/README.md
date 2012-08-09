# POSデータ集計アプリケーション

## 開発環境の準備

Asakusa Frameworkスタートガイド（注1）を参考にして開発環境を準備してください。

（注1. http://asakusafw.s3.amazonaws.com/documents/0.4.0/release/ja/html/introduction/start-guide.html ）

サンプルソースコードをダウンロード（注2）して展開したものに含まれる posdata-summarization ディレクトリを ~/workspace 以下に設置してください。

（注2. https://github.com/asakusafw/asakusafw-tutorials/zipball/master ）

以下のコマンドを実行してサンプルアプリケーションをビルドしてください。ビルドを実行するとソースコードの自動生成やテストが実行されて、「BUILD SUCCESS」と表示されれば、ビルドが完了します。

    cd ~/workspace/posdata-summarization
    mvn assembly:single antrun:run
    cp src/main/resources/posdata-summarization.properties $ASAKUSA_HOME/windgate/profile
    mvn clean package eclipse:eclipse


## バッチ処理の実行

バッチを実行するにはビルドを実行した結果作成されたtarget以下のjarファイル（注3）を$ASAKUSA_HOME/batchapps以下に展開してください。

### バッチアプリケーションのデプロイ

    cp ~/workspace/posdata-summarization/target/*batchapps*.jar $ASAKUSA_HOME/batchapps
    cd $ASAKUSA_HOME/batchapps
    jar xf *batchapps*.jar

（注3.&lt;pom.xmlのartifactId&gt;-batchapps-&lt;pom.xmlのversion&gt;.jarというファイルです）

Asakusa Frameworkには、YAESSというバッチ実行ツールが提供されています。以下のコマンドでバッチアプリケーションを実行します。

    $ASAKUSA_HOME/yaess/bin/yaess-batch.sh PosDataSummarizationBatch -A TARGET_DATE=20120101

「Finished: SUCCESS」と表示されればバッチ処理が正常終了しています。

/tmp/windgate-$USER/result に単品別・カテゴリ別・店舗別に集計されたCSVデータが出力されます。

