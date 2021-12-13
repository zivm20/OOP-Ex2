# OOP-Ex2
 
Instructions:

To load a json file place the json file in the data folder then you may call use the command "java -jar Ex2.jar <your json>" to run the gui


Controls:

t - enter selection mode, click with your mouse on nodes and when you are done click t again, this will highlight the path found using the tsp algorithm with the nodes you selected
p - click 2 nodes and it will automatically show the fastest path
c - highlight the center node


Information regarding the program:

In order to have high insertion speeds and high search speeds of nodes and edges on the graph I used the hashmap data structure so that each node is represented as a key-value pair where the key is the key of the node itself
and the value is an object that contains the node itself and 2 hashmaps, one for the children and one for the parents of the node, each entry in both hashmaps is made like so

parents hashmap: <parent node id (Integer), edge from parent to this node (EdgeData)>
children hashmap: <child node id (Integer), edge from this node to child node (EdgeData)>

This allows us to get O(1) for insertion and search of edges and nodes, O(1) for deletion of edges and O(k) for node V.degree = k



For the graph algorithems i used the following algorithms:
floyd-warshall algorithm to find the center node in the graph: http://www.graph-magics.com/articles/center.php
dynamic programing solution for the Traveling Salesperson Problem: https://www.youtube.com/watch?v=XaXsJJh-Q5Y
simple dfs to find the fastest path
Kosaraju’s DFS based algorithm to check if the graph is connected: https://www.geeksforgeeks.org/connectivity-in-a-directed-graph/



