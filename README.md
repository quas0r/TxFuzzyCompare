#### To Run:  
Download or clone the project, from the folder containing pom.xml, run the following  
**mvn package && java -jar target/tutukaTxCompare-0.1.0.war**  
Application should be reachable at *http://localhost:8989/app/upload*  

# TransactionCompare
Fuzzy match two transaction report files – one from Tutuka and other from a Client. There could be mismatches due to discrepancies in the KYC process/front-end user entered values and hence calls for a fuzzy cross matching.


![alt text](https://raw.githubusercontent.com/quas0r/TxFuzzyCompare/master/TutukaTxCompare_FlowDiag.PNG "Flow Diagram")


### Assumptions:
1.	Transaction ID is the unique ID and only repeats in case of a reversal. Hence, the composite primary key **Tkey = Transaction_ID+TransactionDescription**
2.	If Transaction ID or Transaction Description is NULL/ZERO, then the transaction is marked as BAD_TRANSACTION and will not be fuzzy matched with the transactions in the other file
3.	If the Tkey is not present in the other file, it is marked as UNMATCHED
4.	Only if Tkey matches, it qualifies for a fuzzy matching.
5.	Transaction Narrative field is an address field and will have to be fuzzy matched. As almost NONE of the values from the files [TutukaMarkoffFile20140113.csv & ClientMarkoffFile20140113.csv] resolved to a valid location, geo-location fuzzy matching was out of the equation.
6.	Also, assumed was that the field will contain maximum noise and hence, resorted to look at this column as a regular string valued column. 
7.	Client transactions may be logged in with their local time and hence, there might be time zone differences and two very different TransactionDate fields may point the same time. Since there was no information on the client’s time zone, assumption was made that the client would be in Africa too and a tolerance level of 180 minutes[largest time zone difference across Africa] was added in the fuzzy logic match
8.  **For those transactions that have different TxIDs but other transactions do match mostly, are still considered as UNMATCHED as TxID is an unique ID and a null value of which is considered a BAD_TRANSACTION. Reason for this is TxID should be a generated value for every transaction and cannot be the same for two different transactions. It is better NOT TO match and show up in the report as UNMATCHED and end up as a bug on the client side caught late in the cycle than to treat it as a typo**
</br>
</br>
Transactions are categorized into  

| STATUS        | DESCRIPTION           |  
| ------------- |-------------|  
| PERFECT_MATCH      | Needs no review |  
| PERMISSIBLE_MATCH      | Needs no review [there are mismatches but contextually tolerant] |  
| PROBABLE_MATCH      | 	Needs Review but system predicts a match |  
| PROBABLE_MISMATCH      | Needs Review and system predicts a mismatch |  
| PERFECT_MISMATCH      | TxId and TxDescr do match but too many other factors don't match |  
| BAD_TRANSACTION      | Some required values are null |  
| DUPLICATE      | Duplicate transactions |  
| UNMATCHED      | Unmatched Transactions |    

  
 </br>
  
  **Phonetic Cosine Matrix Method**
  For more Precision, below method could also be employed. However, the linear method also provided a good enough result.  
  Note: score is the cosine similarity score between the BedierMorse encoding of corresponding Rows and Cols
  </br>
 ![alt text](https://raw.githubusercontent.com/quas0r/NearestNeighborKDTreeSearch/master/tutukaCosine.PNG "Phonetic Cosine Matrix Method")
    
</br>
Below properties can be changed to acomodate any additional fields in the future.  
The values were arrived by a lousy ML algorithm but holds good for most scenarios. 

fuzzy.match_tolerance=0.96  
fuzzy.probable_match_tolerance=0.90  
fuzzy.probable_mismatch_tolerance=0.80  
fuzzy.valid_incr=1.00  
fuzzy.fuzzy_incr=0.75  
fuzzy.tx_nrtv_valid_incr=1.50  
fuzzy.tx_nrtv_probable_match_incr=1.25  
fuzzy.tx_nrtv_probable_mismatch_incr=1.00  
fuzzy.total_score=6.50  
fuzzy.permissible_match_lower=6.00  
fuzzy.probable_match_lower=5.50  
fuzzy.probable_mismatch_lower=5.00  
</br>
### Screenshots  

![alt text](https://raw.githubusercontent.com/quas0r/NearestNeighborKDTreeSearch/master/uploadForm.PNG "HomePage")  
  
  
  
  
![alt text](https://raw.githubusercontent.com/quas0r/NearestNeighborKDTreeSearch/master/reportPage.png "Report")  
