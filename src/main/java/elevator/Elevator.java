package elevator;

import java.util.*;

import static elevator.ElevatorState.*;

@SuppressWarnings("Duplicates")
public class Elevator
{
	final int numFloors;
	final int elevatorNumber;

	ElevatorState elevatorState;
	int currentFloor;
	Set<Integer> floorsToStopOn;
	int numTrips = 0;

	Monitor monitor;

	public Elevator(
			Monitor monitor,
			int numFloors,
			int elevatorNumber)
	{
		this.elevatorNumber = elevatorNumber;
		this.monitor = monitor;
		this.numFloors = numFloors;
		floorsToStopOn = new HashSet<>(numFloors);

		currentFloor = 1;
		elevatorState = waitingForPassengers;
	}

	void addFloorToStopOn(int destinationFloor)
	{
		if (elevatorState.isGoingUp && destinationFloor < currentFloor)
			return;

		if (elevatorState.isGoingDown && destinationFloor > currentFloor)
			return;

		floorsToStopOn.add(destinationFloor);
	}

	void openDoors()
	{
		monitor.report("Open doors", elevatorNumber, currentFloor);
	}

	void closeDoors()
	{
		monitor.report("Close doors", elevatorNumber, currentFloor);
	}

	void changeState(ElevatorState newState)
	{
		// todo: Consolidate some of these cases.
		switch(newState)
		{
			case waitingForPassengers:
				switch(elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						// do nothing
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						closeDoors();
						break;
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case waitingForService:
				switch(elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						// do nothing
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Elevator not allowed to move while in service.");
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case movingUpWhileEmpty:
				switch(elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						numTrips++;
						openDoors();
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Bad state change. From " + elevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case movingDownWhileEmpty:
				switch(elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						numTrips++;
						openDoors();
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Bad state change. From " + elevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case movingUpWhileOccupied:
				switch(elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						numTrips++;
						openDoors();
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Bad state change. From " + elevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case movingDownWhileOccupied:
				switch(elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						numTrips++;
						openDoors();
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Bad state change. From " + elevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + newState);
		}
		elevatorState = newState;
	}

	// Elevators can only move during a tick event.
	public void tick()
	{
		switch(elevatorState)
		{
			case waitingForPassengers:
			case waitingForService:
				if (!floorsToStopOn.isEmpty())
				{
					// todo: am I on the right floor?
					// todo: do I need to go up?
					// todo: do I need to go down?
				}
				break;
			case movingUpWhileEmpty:
				// todo
				break;
			case movingDownWhileEmpty:
				// todo
				break;
			case movingUpWhileOccupied:
				// todo
				break;
			case movingDownWhileOccupied:
				// todo
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
		}
	}
}
