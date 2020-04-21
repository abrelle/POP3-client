# POP3-client
A simple pop3 protocol client implementation

Post Office Protocol - Version 3 kliento protokolas

## Implementuotos komandos:

1.STAT - žinučių skaičius<br/>
2.QUIT - ištrina pažymėtas žinutes, atsijungia nuo serverio<br/>
3.USER ir PASS - autentifikacijos etapui<br/>
4.LIST - žinučių sąrašas<br/>
5.RETR - atspausdina žinutės turinį<br/>
6.DELE - pažymi žinutes, kurias norima ištrinti<br/>
7.NOOP - implementuota, tačiau nėra tarp vartotojo pasirinkimų<br/>
8.RSET - atžymi pažymėtas žinutes<br/>
9.TOP - atspausdina pasirinktos žinutės norimą eilučių skaičių<br/>
10.UIDL - atspausdina žinutės unikalų ID.<br/>

Klientas veikia "pop.gmail.com" serveryje. Norint prisijungt prie savo gmail pašto dėžutes, reikia paskytoje įgalinti POP.
Default port - 995, o ne 110, kaip nurodyta RFC 1939, nes dauguma paštų naudoja SSL sertifikatą.

Bandžiau yahoo.com, outlook.com paštus, pavykdavo prisijungti prie serverio, tačiau prie asmeninio pašto - ne.

testPOP3Client.java - testavimui skirtas failas.
POP3Client.java - POP3 protokolo kliento implementacija.

Testavimas: sukompiliavus ir paleidus testPOP3Client.* prašoma suvesti prisijungimo vardą ir slaptažodį (gmail'e įgalinus POP). Po sėkmingo prisijungimo komandinėje eilutėje pasirodo meniu pasirinkimai.
