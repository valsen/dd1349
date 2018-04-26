#### Instructions for creating text files for subway lines
Use the python script and the supplied image to generate coordinates. 
It is VERY IMPORTANT that the stations are added in the correct order, and that the 
connection text file of the subway line corresponds to this order. The following 
is the correct way to do it (the backslashes in the file is markdown formatting): 

Skarpnäck/0.9221789883268483/0.8421658986175116  \
Bagarmossen/0.8852140077821011/0.793778801843318 \
Kärrtorp/0.8589494163424124/0.761520737327189 \
Björkhagen/0.830739299610895/0.7304147465437788 \
Hammarbyhöjden/0.8035019455252919/0.6970046082949308 \
Skärmarbrink/0.7645914396887159/0.6624423963133641 

Skarpnäck/Bagarmossen \
Bagarmossen/Kärrtorp \
Kärrtorp/Björkhagen \
Björkhagen/Hammarbyhöjden \
Hammarbyhöjden/Skärmarbrink 

If the order of the connections does not match the stations, 
the trains will not move to adjacent rails. 