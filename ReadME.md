# Secure Approximate Edit Distance for Genomic Data

This approach contains two solutions both in java (maven built). One uses Shingles and Private Set Intersection (PSI) and another one utilized k-banded alignment in Garbled Circuit. However both of them can be pipelined for better and faster results as experimental results show. The first solution reduces the search space for the actual results and gives an approximation. This approximated result more refined by the second approach as it sorts them accordingly with a variant of faster Edit distance. Both of the approaches provides Top-K results which can be found in Results_PSI.txt and Results_KBandedEditDistance.txt (sequentially).

The first approach uses multiple techniques along with the state of the art PSI (http://eprint.iacr.org/2015/634) which seems the fastest. It provides an approximation of the original edit distance which is outputted in "Results_PSI.txt".  I changed the PSI which is implemented in c++ accordingly to my java program and pipelined it in.

The second approach uses Garbled Circuit (FlexSC) and implements a variant of Edit distance which reduces the runtime of the original algorithm O(nm) to O(nk) where k is the band size. I experimented on this value and fixed it as 10 on the solution. This value of K can be further experiemented. The main advantage it gives is the time required for approximating the edit distance, for example, a sequeqnce with 3000 SNPs usually takes 10mins in edit distance on a garbled circuit while on the KBanded Edit Distance, it takes around 5-10 seconds.

The solution is tested for the data set provided and a synthetic dataset "syntheticDB" attached which contains higher number of SNPs and records as well. To generate the results the scripts "KBanded.KbandedAlignment", "KBanded.KbandedAfterPSI" and "Shingles.ShinglesTest" can be used as those work without security and are much faster.
## Config File
The Config file (Config.conf) has these:
  - Server: 127.0.0.1
  - Port: 54343
  - SecondPort: 54575
  - GramSize:10
  - PSIProtocol:1
  - Limit:40
  - Mode: OPT
  - SearchSpaceShorten:1
   
 About these parameters:
PSIProtocol can be 0, 1, 2 where 0-naive hash(fastest), 1-ot hash, 2-permutation hash(USENIX 2015)
Limit defines the k as in top-k (ignore)
SearchSpaceShorten:ignore this
Gram size is the size of the shingles that are created (also known as w for w-shingles)
Mode is for the GC of FlexSC.
SecondPort is for PSIProtocol

As its a maven built project, the commands to compile
>mvn clean & mvn compile

## Running the Program
```
./runserver.sh db1.fa 2 & ./run_researcher.sh query.fa 2 127.0.0.1 20
```
Here on run_server parameters are db.fa which is the file containing the database and 1/2 stands protocol 1 or both
run_researcher parameters are query.fa which is the file containing the query, 1/2 stands protocol 1 or both, ip of the server and 20 is the limit.

However to change the server ip, the PSI project needs to be rebuilt which is available in PSI folder and taken from USENIX 2015. See [https://github.com/encryptogroup/PSI](https://github.com/encryptogroup/PSI) 


## Result Verification
Accuracy.OriginalEditdistance gets the original edit distance result which takes the db.fa, query.fa as an input while top-k's k is defined as 40 in -Dexec.args="db.fa query.fa 40"
This will output a file "original_results" which will be used for the error calculation with the 2 approximation algos

>mvn exec:java -Dexec.mainClass="Accuracy.ErrorCalcStat" -Dexec.args="db.fa query.fa 40"

Accuracy.ErrorCalcStat script will give the values for TPR, FPR (and some the other statistical measures) for two files where the first one is the original distance file and the second one is the output from the approximation. 

>mvn exec:java -Dexec.mainClass="Accuracy.ErrorCalcStat"

Two example files (82_original.txt and 82_approx1.txt) are given for accuracy check which can be generated also from  "KBanded.KbandedAlignment", "KBanded.KbandedAfterPSI" and "Shingles.ShinglesTest" scripts.



![alt text][logo]

[logo]: http://www.cs.umanitoba.ca/~bruce/umanlogotight.jpg "University of Manitoba Computer Science"
	