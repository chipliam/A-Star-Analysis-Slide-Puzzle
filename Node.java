public class Node {
    //Keeps track of the heuristic value
    public int heur;
    //Keeps track of the depth of the node (added with the heuristic to make the evaluation function)
    public int cost;
    //Keeps track of the state of the puzzle
    public int[][] state;
    //Keeps track of the moves made to get to the current state
    public String path;

    public Node(int heur, int cost, int[][] state, String path){
        this.heur = heur;
        this.cost = cost;
        this.state = state;
        this.path = path;
    }

}
