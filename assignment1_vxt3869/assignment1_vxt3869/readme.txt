Name: Vinayak Ravindra Tare
UTA ID: 1001453869
Programming language: Java

Code Structure:
Node class{
name: city name
pathCost: stores cost to that city from source
al: stores direct roads and distance from this city
parent: stores previous city in a path
}

Edge class{
target: stores city name to which there is exists direct route to city 
cost: distance between given city and target
}

find_route class{
This class contains main method. It takes three command line arguments first filename.txt second source third destination
FILENAME: stores filename
rawFile: stores entire file in arraylist as strings
nodeSet: stores set of cities(nodes).
source: stores source city.
destination: stores destination city.
routeSearch method: computes path from source to destination
getPath: returns all the cities that are on shortest path from source to destination.

prints total distance from source to destination and 
all the cities in the path and distance between each one of them.
}

routeSearch method{
fringe: priority queue to determine next city in consideration depending on cost to travel
visited: stores list of cities already visited.

this methods computes shortest path from source to destination if path exists.
}

find_node method{
this method simply traverse through nodeSet and returns city(node) matching given city name
}

getPath method{
this method returns path(all the cities on shortest path from source to destination).
}

How to run the code:

Step 1: Unzip the assignment1_vxt3869
Step 2: open command prompt
Step 3: Make current directory as assignment1_vxt3869(cd \Users\Vinayak Tare\Desktop\\assignment1_vxt3869)
Step 4: Type java to make sure java setup
Step 5: javac find_route.java (compile the code)
Step 6: java find_route input1.txt Luebeck Munich (java find_route filename.txt source destination) 
(if u want to try some other map, make sure .txt file and find_route.java is in same directory)

C:\Users\Vinayak Tare\Desktop\assignment1_vxt3869>javac find_route.java

C:\Users\Vinayak Tare\Desktop\assignment1_vxt3869>java find_route input1.txt Luebeck Munich
Output:
Distance 959.0 km
Route
Luebeck  to  Hamburg 63.0 km
Hamburg  to  Hannover 153.0 km
Hannover  to  Kassel 165.0 km
Kassel  to  Frankfurt 185.0 km
Frankfurt  to  Nuremberg 222.0 km
Nuremberg  to  Munich 171.0 km

C:\Users\Vinayak Tare\Desktop\assignment1_vxt3869>






