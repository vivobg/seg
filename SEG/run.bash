#!/bin/bash

javac /src/MainApp.java

player ./mapconf/map2.cfg & cd bin/; sleep 2; java MainApp -gui && fg

cd ..
