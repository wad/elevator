package elevator;

public class ElevatorSimulation
{
	ElevatorController elevatorController;

	public ElevatorSimulation(
			int numElevators,
			int numFloors)
	{
		elevatorController = new ElevatorController(numElevators, numFloors);
	}

	public void tick()
	{
		elevatorController.tick();
	}

	public void callForRide(
			int fromFloor,
			boolean wantsToGoUp)
	{
		elevatorController.addRequest(new ElevatorCallRequest(fromFloor, wantsToGoUp));
	}

	public void riderRequest(
			int elevatorNumber,
			int toFloor)
	{
		elevatorController.riderRequest(elevatorNumber, toFloor);
	}

	public void performService(int elevatorNumber)
	{
		elevatorController.markServiceIsComplete(elevatorNumber);
	}

	public void finalReport()
	{
		elevatorController.finalReport();
	}

	// Sample run
	public static void main(String... args)
	{
		int numElevators = 3;
		int numFloors = 10;
		ElevatorSimulation sim = new ElevatorSimulation(numElevators, numFloors);
		sim.tick();
		sim.riderRequest(0, 10);
		sim.riderRequest(1, 5);
		sim.tick();
		sim.callForRide(3, true);
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.finalReport();
	}
}

/*
Output for the sample run:

First argument is how many elevators, second argument is how many floors.
Elevator controller started with 3 elevators and 10 floors
tick 0
Elevator 0 on floor 1: Elevator state waitingForPassengers
Elevator 1 on floor 1: Elevator state waitingForPassengers
Elevator 2 on floor 1: Elevator state waitingForPassengers
Rider in elevator 0 pushed floor button 10
Rider in elevator 1 pushed floor button 5
tick 1
Elevator 0 on floor 1: Close doors
Elevator 0 on floor 1: Changed state from waitingForPassengers to movingUpWhileOccupied
Elevator 0 on floor 1: Elevator state movingUpWhileOccupied
Elevator 1 on floor 1: Close doors
Elevator 1 on floor 1: Changed state from waitingForPassengers to movingUpWhileOccupied
Elevator 1 on floor 1: Elevator state movingUpWhileOccupied
Elevator 2 on floor 1: Elevator state waitingForPassengers
Adding request: Person on floor 3 pushed the up button
tick 2
Handling request: Person on floor 3 pushed the up button
Elevator 2 was selected to receive call request.
Elevator 0 on floor 1: Moved up one level to floor 2
Elevator 0 on floor 2: Elevator state movingUpWhileOccupied
Elevator 1 on floor 1: Moved up one level to floor 2
Elevator 1 on floor 2: Elevator state movingUpWhileOccupied
Elevator 2 on floor 1: Close doors
Elevator 2 on floor 1: Changed state from waitingForPassengers to movingUpWhileEmpty
Elevator 2 on floor 1: Elevator state movingUpWhileEmpty
tick 3
Elevator 0 on floor 2: Moved up one level to floor 3
Elevator 0 on floor 3: Elevator state movingUpWhileOccupied
Elevator 1 on floor 2: Moved up one level to floor 3
Elevator 1 on floor 3: Elevator state movingUpWhileOccupied
Elevator 2 on floor 1: Moved up one level to floor 2
Elevator 2 on floor 2: Elevator state movingUpWhileEmpty
tick 4
Elevator 0 on floor 3: Moved up one level to floor 4
Elevator 0 on floor 4: Elevator state movingUpWhileOccupied
Elevator 1 on floor 3: Moved up one level to floor 4
Elevator 1 on floor 4: Elevator state movingUpWhileOccupied
Elevator 2 on floor 2: Moved up one level to floor 3
Elevator 2 on floor 3: Elevator state movingUpWhileEmpty
tick 5
Elevator 0 on floor 4: Moved up one level to floor 5
Elevator 0 on floor 5: Elevator state movingUpWhileOccupied
Elevator 1 on floor 4: Moved up one level to floor 5
Elevator 1 on floor 5: Elevator state movingUpWhileOccupied
Elevator 2 on floor 3: Open doors
Elevator 2 on floor 3: Changed state from movingUpWhileEmpty to waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 6
Elevator 0 on floor 5: Moved up one level to floor 6
Elevator 0 on floor 6: Elevator state movingUpWhileOccupied
Elevator 1 on floor 5: Open doors
Elevator 1 on floor 5: Changed state from movingUpWhileOccupied to waitingForPassengers
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 7
Elevator 0 on floor 6: Moved up one level to floor 7
Elevator 0 on floor 7: Elevator state movingUpWhileOccupied
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 8
Elevator 0 on floor 7: Moved up one level to floor 8
Elevator 0 on floor 8: Elevator state movingUpWhileOccupied
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 9
Elevator 0 on floor 8: Moved up one level to floor 9
Elevator 0 on floor 9: Elevator state movingUpWhileOccupied
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 10
Elevator 0 on floor 9: Moved up one level to floor 10
Elevator 0 on floor 10: Elevator state movingUpWhileOccupied
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 11
Elevator 0 on floor 10: Open doors
Elevator 0 on floor 10: Changed state from movingUpWhileOccupied to waitingForPassengers
Elevator 0 on floor 10: Elevator state waitingForPassengers
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
Elevator 0: Final report: numTripsSinceLastService=1 totalNumTrips=1 totalFloorsPassed=9
Elevator 1: Final report: numTripsSinceLastService=1 totalNumTrips=1 totalFloorsPassed=4
Elevator 2: Final report: numTripsSinceLastService=1 totalNumTrips=1 totalFloorsPassed=2
*/
