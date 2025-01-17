import java.util.ArrayList;
import java.util.LinkedList;
import java.util.*;
import java.util.function.Supplier;

public class Main {

    public static void main(String[] args) {
        //Initialize world
        Map<String, ArrayList<int[]>> frontier = new HashMap<>();
        LinkedList<String> frontierIDsQueue = new LinkedList<>(); //to hold the order
        Set<String> explored = new HashSet<>();

        List<Supplier<ArrayList<int[]>>> actions = Arrays.asList(
                () -> move(frontier, frontierIDsQueue),
//                () -> moveLeft(frontier, frontierIDsQueue),
//                () -> moveRight(frontier, frontierIDsQueue),
                () -> vacuum(frontier, frontierIDsQueue));

        ArrayList<int[]> initial_state = new ArrayList<>();
        int[] dust = {1, 1}; // 1 = there's dust, 0 = there's no dust
        int[] robotCoords={0}; //possible values 0 (left) and 1 (right)
        initial_state.add(dust);
        initial_state.add(robotCoords);

        frontierIDsQueue.add(generateStateID(initial_state));
        frontier.put(frontierIDsQueue.peek(), initial_state);
        while(!frontier.isEmpty()){
            ArrayList<int[]> current_state = frontier.get(frontierIDsQueue.peek());
            System.out.print("\nEXPANDING state ");
            printState(current_state);
            System.out.println();
            for(int i=0; i<2; i++) {
                ArrayList<int[]> neighbour=actions.get(i).get();
                if(neighbour!=null){ //if the action leads to a new state (doesn't loop back to the same)
                    if (isGoalState(neighbour)) { //if it's a goal
                        return;
                    }else{
                        String stateID=generateStateID(neighbour);
                        if(!frontierIDsQueue.contains(stateID) && !explored.contains(stateID)) {//if previously unreached state
                            //add new state to frontier
                            frontier.put(stateID, neighbour);
                            frontierIDsQueue.add(stateID);
                            System.out.println("\tNEW STATE");
                            printState(neighbour);
                        }else{
                            System.out.println("\tALREADY VISITED");
                        }
                    }
                }
            }
            //at the end, remove expanded state from frontier and add it to explored
            frontier.remove(frontierIDsQueue.peek());
            explored.add(frontierIDsQueue.peek());
//            System.out.print("\tExplored set: ");  //for debugging
//            printSet(explored);
            frontierIDsQueue.poll();
//            System.out.print("\tFrontier: ");     //for debugging
//            printLinkedList(frontierIDsQueue);
        }
    }

    public static ArrayList<int[]> move(Map<String, ArrayList<int[]>> frontier, LinkedList<String> queue){
        ArrayList<int[]> stateIn = frontier.get(queue.peek());
        ArrayList<int[]> childState = new ArrayList<>(); //clone state
        for (int[] arr : stateIn) {
            childState.add(arr.clone());
        }
        childState.get(1)[0] = (-1)*(childState.get(1)[0]-1);
        System.out.print("\tMove -> "+generateStateID(childState)+"\t");
        return childState;
    }

    public static ArrayList<int[]> vacuum(Map<String, ArrayList<int[]>> frontier, LinkedList<String> queue) {
        ArrayList<int[]> stateIn = frontier.get(queue.peek());
        if(stateIn.get(0)[stateIn.get(1)[0]]!=0) { //if the dust value at the robot coords is !0
            ArrayList<int[]> childState = new ArrayList<>();
            for (int[] arr : stateIn) {
                childState.add(arr.clone());
            }
            childState.get(0)[childState.get(1)[0]] = 0;
            System.out.print("\tVacuum -> "+generateStateID(childState));
            return childState;
        }else{
            System.out.println("\tCan't vacuum");
            return null; //flag to avoid looping to the same state if the action has no effect
        }
    }

    public static boolean isGoalState(ArrayList<int[]> stateIn){
        for (int i = 0; i<stateIn.get(0).length; i++){
            if (stateIn.get(0)[i]!=0){ //if any value representing dust == 1
                return false;
            }
        } //if we didn't exit before it means all dust values are 0
        System.out.println("\t!!GOAL REACHED!!");
        printState(stateIn);
        return true;
    }

    static String generateStateID(ArrayList<int[]> state){
        StringBuilder stateIDBuild = new StringBuilder();
        //Generate ID by coping the values in the state in order. The final combination will be unique. First two digits represent dust location, last digit represent robot coordinate
        //Because the ID is significant (representative of something/interpretable) and not randomly generated, I could use this instead of the ArrayList as structure to hold the states,
        //but the ArrayList structure is more easily interpretable in bigger worlds
        for (int[] array : state) {
            for (int i=0; i< array.length; i++){
                stateIDBuild.append(array[i]);
            }
        }
        String stateID=stateIDBuild.toString();
        switch (stateID){ //included only for more easily following along (labels from graph given). Delete if no need to see/debug the process (especially in larger worlds)
            case "110" : stateID=stateID+" (1)"; break;
            case "111" : stateID=stateID+" (2)"; break;
            case "010" : stateID=stateID+" (3)"; break;
            case "011" : stateID=stateID+" (4)"; break;
            case "100" : stateID=stateID+" (5)"; break;
            case "101" : stateID=stateID+" (6)"; break;
            case "000" : stateID=stateID+" (7)"; break;
            case "001" : stateID=stateID+" (8)"; break;
            default: stateID="error";
        }
        return stateID;
    }

    public static void printState(ArrayList<int[]> stateIn){
        System.out.println("\tID "+generateStateID(stateIn)+":");

        System.out.println("\t| "+stateIn.get(0)[0] + " | "+ stateIn.get(0)[1]+" |\tDust");
        System.out.print("\t| ");
        for (int i=0; i< stateIn.get(0).length; i++){
            if(i==stateIn.get(1)[0]) {
                System.out.print("x"+" | ");
            }else{
                System.out.print(" "+" | ");
            }
        }
        System.out.print("\tRobot position\n");
    }

    public static void printSet(Set<String> set){
        Iterator<String> itr = set.iterator();
        while (itr.hasNext()) {
            System.out.print(itr.next()+", ");
        }
        System.out.println();
    }

    public static void printLinkedList(LinkedList<String> list) {
        for (String element : list) {
            System.out.print(element+", ");
        }
    }

// Unused: For more general worlds with more than two position-slots
    public static ArrayList<int[]> moveLeft(Map<String, ArrayList<int[]>> frontier, LinkedList<String> queue){
        ArrayList<int[]> stateIn = frontier.get(queue.peek());
        if (stateIn.get(1)[0]!=0) { //if the robot is not in the leftmost coordinate
            ArrayList<int[]> childState = new ArrayList<>(); //clone state
            for (int[] arr : stateIn) {
                childState.add(arr.clone());
            }
            childState.get(1)[0] += -1; //move left and record the change in the new state
            System.out.println("Move Left -> "+generateStateID(childState));
            return childState;
        }else{
            System.out.println("Can't move left");
            return null; //flag to avoid looping to the same state if the action has no effect
        }
    }
    public static ArrayList<int[]> moveRight(Map<String, ArrayList<int[]>> frontier, LinkedList<String> queue){
        ArrayList<int[]> stateIn = frontier.get(queue.peek());
        if (stateIn.get(1)[0]!=stateIn.get(0).length-1) {
            ArrayList<int[]> childState= new ArrayList<>();
            for (int[] arr : stateIn) {
                childState.add(arr.clone());
            }
            childState.get(1)[0] += 1; // move right
            System.out.println("Move right -> "+generateStateID(childState));
            return childState;
        }else{
            System.out.println("Can't move right");
            return null; //flag to avoid looping to the same state if the action has no effect
        }
    }
//        public static boolean[] check_actions(LinkedList<ArrayList<int[]>> frontier){
//        ArrayList<int[]> stateIn= frontier.peek();
//        boolean canMoveLeft= true, canMoveRight=true, canVacuum  = true;
//        boolean[] actions = {canMoveLeft, canMoveRight, canVacuum};
//        if (stateIn.get(1)[0]==0){
//            actions[0]=false; //can't move left
//        }
//        if (stateIn.get(1)[0]==stateIn.get(0).length-1) { //if the robot is in the rightmost coordinate
//            actions[1]=false; //can't move right
//        }
//        if (stateIn.get(0)[stateIn.get(1)[0]]==0){ //if there's no dust at the robot coords
//            actions[2]=false; //can't vacuum
//        }
//        return actions;
//    }


}