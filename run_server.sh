#!/bin/bash
# $1= db.fa file containing the database
# $2= 1/2 protocol 1 or both
#./run_server.sh db.fa 2
echo $1  $2 
#echo "maven cleaning and compiling, log in tmp.log"
#mvn -q clean > tmp.log
#mvn -q compile > tmp.log
SECONDS=0
mvn exec:java -q -Dexec.mainClass="cs.umanitoba.idashtask2.PSI.PreProcessPSI" -Dexec.args="$1" 
duration_preprocessing=$SECONDS
SECONDS=0
mvn exec:java -q  -Dexec.mainClass="cs.umanitoba.idashtask2.PSI.DataOwnerPSI" -Dexec.args="$1"
#& sleep 4 & mvn exec:java -Dexec.mainClass="cs.umanitoba.idashtask2.PSI.ResearcherPSI" -Dexec.args="$2" 

if [ "$2" == "1" ]; then
	echo "$(($duration_preprocessing / 60)) minutes and $(($duration_preprocessing % 60)) seconds for preprocessing"
	echo "protocol 1 ended, results in Results_PSI.txt"
	duration=$SECONDS
	
else 
	#KBanded alignment in Garbled Circuit
	#Preprocess the data for data owner and researcher
	SECONDS2=0
	mvn exec:java -q -Dexec.mainClass="cs.umanitoba.idashtask2.PreProcessKBandedServer" -Dexec.args="$1" 
	duration_preprocessing=$duration_preprocessing+$SECONDS2
	mvn exec:java -Dexec.mainClass="cs.umanitoba.idashtask2.DataOwner" 
	#& sleep 1 & 	mvn exec:java -Dexec.mainClass="cs.umanitoba.idashtask2.Researcher" -Dexec.args="$2"
	duration=$SECONDS
	echo "protocol 2 ended, results in Results_KBandedEditDistance.txt"
	echo "$(($duration_preprocessing / 60)) minutes and $(($duration_preprocessing % 60)) seconds for total preprocessing"
fi
echo "$(($duration / 60)) minutes and $(($duration % 60)) seconds elapsed for protocol $2."