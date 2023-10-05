Noémie Arbour
Nhung Dang Thi Hong

Lien GitHub : https://github.com/NoemieArbour/IFT3913_TP1.git

# Compiler et exécuter :

## TLOC

java <PATH TO TLOC>.java <PATH TO A TEST FILE>.java

ou, avec le ficher jar,

java -jar <PATH TO TLOC>.jar <PATH TO A TEST FILE>.java

où <PATH TO TLOC>.java est .\tloc\src\main\java\ift3913\tloc.java à partir du root du projet.


## TASSERT

java <PATH TO TASSERT>.java <PATH TO A TEST FILE>.java

ou, avec le ficher jar,

java -jar <PATH TO TASSERT>.jar <PATH TO A TEST FILE>.java

où <PATH TO TASSERT>.java est .\tassert\src\main\java\ift3913\tassert.java à partir du root du projet.


## TLS

java <PATH TO TLS>.java <PATH TO A JAVA TEST FILE OR FOLDER>
java <PATH TO TLS>.java -o <OUTPUT>.csv <PATH TO A JAVA TEST FILE OR FOLDER>

ou, avec le ficher jar,

java -jar <PATH TO TLS>.jar <PATH TO A JAVA TEST FILE OR FOLDER>
java -jar <PATH TO TLS>.jar -o <OUTPUT>.csv <PATH TO A JAVA TEST FILE OR FOLDER>

où <PATH TO TLS>.java est .\tls\src\main\java\ift3913\tls.java à partir du root du projet.


## TROPCOMP

java <PATH TO TROPCOMP>.java <PATH TO A JAVA TEST FILE OR FOLDER>
java <PATH TO TROPCOMP>.java -o <OUTPUT>.csv <PATH TO A JAVA TEST FILE OR FOLDER>

ou, avec le ficher jar,

java -jar <PATH TO TROPCOMP>.jar <PATH TO A JAVA TEST FILE OR FOLDER> <SEUIL%>
java -jar <PATH TO TROPCOMP>.jar -o <OUTPUT>.csv <PATH TO A JAVA TEST FILE OR FOLDER> <SEUIL%>

où <PATH TO TROPCOMP>.java est .\tropcomp\src\main\java\ift3913\tropcomp.java à partir du root du projet.

Pour TROPCOMP, le seuil doit être en pourcentage : pour 10%, le seuil est 10 (et non 0.1).
