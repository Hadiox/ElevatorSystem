ELEVATOR SYSTEM

Made by Bartłomiej Łagosz

Utility methods specified in javadocs.

DESCRIPTION OF THE SYSTEM

The system provides an interface (used in a simulation):

ElevatorCalling callElevator(Integer floor,Integer direction) - calls an elevator on a specified floor and informs the
system about direction where passengers want to go after entering the elevator on desired floor.

void moveElevator(Elevator elevator) - moves an elevator up or down or operates processing an elevator.

void printStatus() - Shows the status of whole system of elevators within the step (actual floor
of elevator, its task queue, state and pressed buttons after processing).

void step() - Makes a step of simulation (moving elevators, printing status of the system,
generating new elevator callings from different floors, scheduling new callings).

SCHEDULING ALGORITHM DESCRIPTION

To optimize elevator movements I tried to implement Shortest Job First algorithm but implementation of it for this
system is my own idea.

Each step of simulation generates (may generate) new elevator callings. These are represented as the rows of the
matrix M. Each elevator in a system represents a column of M. An element of M (M(x,y) - x is the calling, y is the
elevator) represents amount of time needed for elevator y to get on a floor specified in calling x. This amount of time
is held in a TimeToComeWrapper. TimeToComeWrapper holds an information about calling, elevator operating the calling,
amount of time to operate the calling and place in queue supposing the calling was operated by the elevator.

How timeToCome and placeInQueue is counted?

Basically the algorithm goes through elevator's task queue and checks whether a calling can be inserted between two
already inserted callings. It can be done if:

RULES:

1. a calling floor is between two neighbouring callings in a queue
2. the calling after insertion won't change direction of the elevator between previously inserted callings

If there is no place between callings in the task queue - the calling is inserted at the end of the queue.

distance = distance to previous callings in queue + actual processing time + processing time on destination floors
of previous callings in queue

Each TimeToComeWrapper in M is also inserted into priority queue (PQ). In PQ TimeToComeWrappers are compared with their
timeToCome values.

After inserting all TimeToComeWrappers of all callings with their elevators to operate - algorithm takes the top element
of PQ and inserts it into elevator task queue - this is the shortest calling which can be operated. After this insertion
a row of matrix which represented the calling is deleted, indexes of the rows are rearranged, inserted
calling's TimeToComeWrappers are removed from PQ and all callings of an elevator which handled inserted calling are
recounted - their times of operation in this elevator could change after insertion.

Then algorithm takes next top element of PQ and repeats the process until PQ is empty.

Algorithm also takes into account passengers entering the elevator. Gets all new pressed buttons - new orders and
inserts each between other callings in task queue if possible (like counting time algorithm, but only rule 1 is
considered). If not - puts them at the end of the queue but in order which ensures the shortest way between two callings
(each time finds the closest floor calling).

COMPLEXITY

The most time-expensive operation is inserting every TimeToComeWrapper into a PQ which gives:

O(E*C*log(E*C))

and counting all timeToCome amounts:

O(C*C*E) - this is very pesimistic - hardly ever all task queues are filled with all possible floors, especially at the
beggining and the more tasks in queues the less tasks to insert into queue

E - number of elevators
C - number of callings (<= number of floors)
E*C - number of TimeToComeWrappers
Inserting into priority queue log(E*C)

Other operations:
Creating matrix: O(E*C)
Reindexing matrix: O(C)
Removing elements from PQ: O(E*log(E*C))
Inserting into task queues/counting timeToCome: O(C) (number of floors)

Algorithm complexity: O(E*C*log(E*C)) (O(C*C*E) is probably never reached)

Disclaimer: I used "calling" and "order" phrase to express the same thing, it it an equivalent, sorry for the
inconvenience.

How to run the simulation?

Program arguments:

First argument (numberOfFloors) - number of floors in simulation
     *             Second argument (numberOfElevators)  - Number of elevators
     *             Third argument (numberOfSteps) - Number of steps of simulation
     *             Fourth argument (maxNumberOfOrdersPerStep) - Maximum number of callings from different floors per
                   step of simulation
     *             Fifth argument (timeToProcess) - Number of steps between elevator opening on an ordered floor and
                   starting moving
     *             Sixth argument (maxNumberOfPressedButtonsInside) - Maximum number of orders for elevator to move on
                   a floor (passengers press buttons inside the elevator


