* Steps to run Sentiment Soccer Manually on EC2 server

-- Crawler --
1) Populate Team collection on MongoDB:
$ java -jar teamsPopulator-jar-with-dependencies.jar

2) Run crawler on background mode
$ nohup java -jar sentiment-soccer-jar-with-dependencies.jar &

3) Keep checking nohup file: 
$ tail -f nohup.out


* Steps to run Sentiment Soccer automatically every 15 minutes (currently is on)

-- Crawler --
1) edit crontab file to schedule crawler_starter.sh to run every 15 minutos 
$ crontab -e
#check how it works on http://www.cyberciti.biz/faq/crontab-every-10-min/

2) check log files on ~/logs/