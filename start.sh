#!/bin/sh

PID_FILE="app.pid"
if [ -f $PID_FILE ]
  then
    if ps -p $(cat $PID_FILE) > /dev/null
        then
          echo "The service already started."
          echo "To start service again, run stop.sh first."
          exit 0
    fi
fi

echo "Starting Journal Application..."

nohup java -jar ./target/StockJournal-0.0.1-SNAPSHOT.jar > JournalLog.out &

echo $! > $PID_FILE