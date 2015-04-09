Step to run Sentiment Soccer on EC2 server

-- Crawler --
1) Populate Team collection on MongoDB:
$ java -jar teamsPopulator-jar-with-dependencies.jar

2) Run crawler on background mode
$ nohup java -jar sentiment-soccer-jar-with-dependencies.jar &

3) Keep checking nohup file: 
$ tail -f nohup.out


