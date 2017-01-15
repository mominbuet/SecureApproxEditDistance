#!/bin/bash
# $1= query.fa file containing the query
# $2= 1/2 protocol 1 or both
# $3 server/dataowner ip
# $4 limit/k as in top k
#./run_researcher.sh query.fa 2 127.0.0.1 30
echo $1 $2 $3 $4
SECONDS=0
mvn exec:java -q  -Dexec.mainClass="cs.umanitoba.idashtask2.PSI.ResearcherPSI" -Dexec.args="$1 $4 $3"

if [ "$2" == "1" ]; then
	echo "$(($SECONDS/60)) minutes and $(($SECONDS % 60)) seconds for preprocessing"
	echo "protocol 1 ended, results in Results_PSI.txt"
	duration=$SECONDS
else 
	#KBanded alignment in Garbled Circuit
	#Preprocess the data for data owner and researcher
	SECONDS2=0
	sleep 5
	mvn exec:java -q -Dexec.mainClass="cs.umanitoba.idashtask2.PreProcessResearcher" -Dexec.args="$3"
	duration_preprocessing=$SECONDS2
	mvn exec:java -q -Dexec.mainClass="cs.umanitoba.idashtask2.Researcher" -Dexec.args="$1 $3"
	duration=$SECONDS
	echo "protocol 2 ended, results in Results_KBandedEditDistance.txt"
	echo "$(($duration_preprocessing/60)) minutes and $(($duration_preprocessing % 60)) seconds for total preprocessing"
fi

echo "$(($duration / 60)) minutes and $(($duration % 60)) seconds elapsed for protocol $2."