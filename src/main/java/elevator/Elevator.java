package elevator;

import java.util.*;

import static elevator.ElevatorState.*;

/*

Notes:
You can't really tell if an elevator is occupied or not. So it just guesses.

todo: handle up/down external buttons
todo: handle case where all elevators are being serviced
 */

@SuppressWarnings("Duplicates")
public class Elevator
{
	final int numFloors;
	final int elevatorNumber;

	ElevatorState elevatorState;
	int currentFloor;
	Set<Integer> floorsToStopOn;
	int numTrips = 0;

	// todo: This is ugly. Find a better way to handle this logic.
	boolean internalRequestReceivedSinceLastTick;

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

	// Someone pushed a button from outside
	public void externalFloorRequest(int destinationFloor)
	{
		addFloorToStopOn(destinationFloor);
	}

	// Someone pushed a button from inside
	public void internalFloorRequest(int destinationFloor)
	{
		// Let's fix the state if needed
		switch(elevatorState)
		{
			case waitingForPassengers:
			case waitingForService:
			case movingUpWhileOccupied:
			case movingDownWhileOccupied:
				break;
			case movingUpWhileEmpty:
				elevatorState = movingUpWhileOccupied;
				break;
			case movingDownWhileEmpty:
				elevatorState = movingDownWhileOccupied;
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
		}

		internalRequestReceivedSinceLastTick = true;
		addFloorToStopOn(destinationFloor);
	}

	void addFloorToStopOn(int destinationFloor)
	{
		boolean elevatorIsStopped = !elevatorState.isGoingUp && !elevatorState.isGoingDown;
		if (elevatorIsStopped && destinationFloor == currentFloor)
			return;

		if (elevatorState.isGoingUp && destinationFloor < currentFloor)
			return;

		if (elevatorState.isGoingDown && destinationFloor > currentFloor)
			return;

		// handle the case where the elevator is in a middle floor, and someone pushes buttons both above and below
		if (!floorsToStopOn.isEmpty())
		{
			boolean selectedFloorIsAboveCurrentFloor = destinationFloor > currentFloor;
			boolean shouldDiscardRequest = shouldGoUp() != selectedFloorIsAboveCurrentFloor;
			if (shouldDiscardRequest)
				return;
		}

		floorsToStopOn.add(destinationFloor);
	}

	boolean shouldGoUp()
	{
		if (floorsToStopOn.isEmpty())
			throw new IllegalStateException("Bug in code! Need to verify there are floors to stop on before calling me.");

		int someFloorToStopOn = (int)floorsToStopOn.toArray()[0];
		return someFloorToStopOn > currentFloor;
	}

	public void serviceIsComplete()
	{
		if (elevatorState == waitingForService)
			changeState(waitingForPassengers);
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
				boolean elevatorNeedsToMove = !floorsToStopOn.isEmpty();
				if (elevatorNeedsToMove)
				{
					// It will actually move next tick. This tick just updates the state.
					if (shouldGoUp())
						changeState(internalRequestReceivedSinceLastTick ? ElevatorState.movingUpWhileOccupied : ElevatorState.movingUpWhileEmpty);
					else
						changeState(internalRequestReceivedSinceLastTick ? ElevatorState.movingUpWhileOccupied : ElevatorState.movingUpWhileEmpty);
				}
			case waitingForService:
				// can't do anything until elevator is serviced
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

		// Blank this out at the end of each tick.
		internalRequestReceivedSinceLastTick = false;
	}
}
