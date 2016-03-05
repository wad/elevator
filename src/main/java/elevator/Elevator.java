package elevator;

import java.util.HashSet;
import java.util.Set;

import static elevator.ElevatorState.*;

@SuppressWarnings("Duplicates")
public class Elevator
{
	static final int MAX_TRIPS_BEFORE_SERVICE = 100;
	static final int BOTTOM_FLOOR = 1;

	final int numFloors;
	final int elevatorNumber;

	int currentFloor;
	ElevatorState currentElevatorState;
	Set<Integer> floorsToStopOn;

	int numTripsSinceLastService = 0;
	int totalNumTrips = 0;
	int totalFloorsPassed = 0;

	// This is so we can identify if an elevator that we thought was unoccupied, turns out to have someone in it.
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
		currentElevatorState = waitingForPassengers;
	}

	// Someone pushed a button from outside, to call this elevator
	public void externalFloorRequest(int destinationFloor)
	{
		addFloorToStopOn(destinationFloor);
	}

	// Someone pushed a button from inside the elevator
	public void internalFloorRequest(int destinationFloor)
	{
		// Let's fix the state if needed
		switch (currentElevatorState)
		{
			case waitingForPassengers:
			case waitingForService:
			case movingUpWhileOccupied:
			case movingDownWhileOccupied:
				break;
			case movingUpWhileEmpty:
				currentElevatorState = movingUpWhileOccupied;
				break;
			case movingDownWhileEmpty:
				currentElevatorState = movingDownWhileOccupied;
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + currentElevatorState);
		}

		internalRequestReceivedSinceLastTick = true;
		addFloorToStopOn(destinationFloor);
	}

	void addFloorToStopOn(int destinationFloor)
	{
		boolean elevatorIsStopped = !currentElevatorState.isGoingUp && !currentElevatorState.isGoingDown;
		if (elevatorIsStopped && destinationFloor == currentFloor)
			return;

		if (currentElevatorState.isGoingUp && destinationFloor < currentFloor)
			return;

		if (currentElevatorState.isGoingDown && destinationFloor > currentFloor)
			return;

		// handle the case where the elevator is in a middle floor, and someone pushes buttons both above and below
		if (!floorsToStopOn.isEmpty())
		{
			boolean selectedFloorIsAboveCurrentFloor = destinationFloor > currentFloor;
			boolean shouldDiscardRequest = elevatorWantsToGoUp() != selectedFloorIsAboveCurrentFloor;
			if (shouldDiscardRequest)
				return;
		}

		floorsToStopOn.add(destinationFloor);
	}

	// This will return TRUE if there are buttons pushed, inside the elevator, that are for floors above the current floor.
	boolean elevatorWantsToGoUp()
	{
		if (floorsToStopOn.isEmpty())
			throw new IllegalStateException("Bug in code! Need to verify there are floors to stop on before calling me.");

		// Just get any stopping floor, because we won't allow stopping floors below the current floor.
		int someFloorToStopOn = (int) floorsToStopOn.toArray()[0];
		return someFloorToStopOn > currentFloor;
	}

	public void markServiceAsComplete()
	{
		if (currentElevatorState == waitingForService)
		{
			numTripsSinceLastService = 0;
			changeState(waitingForPassengers);
		}
	}

	void openDoors()
	{
		monitor.report("Open doors", elevatorNumber, currentFloor);
	}

	void closeDoors()
	{
		monitor.report("Close doors", elevatorNumber, currentFloor);
	}

	void move(boolean goingUp)
	{
		if (goingUp && currentFloor == numFloors)
		{
			changeState(waitingForPassengers);
			return;
		}

		if (!goingUp && currentFloor == BOTTOM_FLOOR)
		{
			changeState(waitingForPassengers);
			return;
		}

		int newCurrentFloor = currentFloor + (goingUp ? 1 : -1);
		monitor.report("Moved " + (goingUp ? "up" : "down") + " one level to floor " + newCurrentFloor, elevatorNumber, currentFloor);

		totalFloorsPassed++;
		currentFloor = newCurrentFloor;
	}

	void changeState(ElevatorState newState)
	{
		switch (newState)
		{
			case waitingForPassengers:
			case waitingForService:
				switch (currentElevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						// do nothing
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						openDoors();
						break;
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + currentElevatorState);
				}
				break;
			case movingUpWhileEmpty:
			case movingDownWhileEmpty:
				switch (currentElevatorState)
				{
					case waitingForPassengers:
						incrementNumTrips();
						closeDoors();
						break;
					case movingUpWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileEmpty:
					case movingDownWhileOccupied:
					case waitingForService:
						throw new IllegalStateException("Bad state change. From " + currentElevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + currentElevatorState);
				}
				break;
			case movingUpWhileOccupied:
				switch (currentElevatorState)
				{
					case waitingForPassengers:
						incrementNumTrips();
						closeDoors();
						break;
					case movingUpWhileEmpty:
						// someone inside pushed a button!
						break;
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
					case waitingForService:
						throw new IllegalStateException("Bad state change. From " + currentElevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + currentElevatorState);
				}
				break;
			case movingDownWhileOccupied:
				switch (currentElevatorState)
				{
					case waitingForPassengers:
						incrementNumTrips();
						closeDoors();
						break;
					case movingDownWhileEmpty:
						// someone inside pushed a button!
						break;
					case movingUpWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
					case waitingForService:
						throw new IllegalStateException("Bad state change. From " + currentElevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + currentElevatorState);
				}
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + newState);
		}

		monitor.report("Changed state from " + currentElevatorState + " to " + newState, elevatorNumber, currentFloor);

		currentElevatorState = newState;
	}

	void incrementNumTrips()
	{
		numTripsSinceLastService++;
		totalNumTrips++;
	}

	// return null if the elevator will not be able to respond.
	public Integer howManyFloorsAreYouFromBeingAbleToStopHere(ElevatorCallRequest elevatorCallRequest)
	{
		int destinationFloor = elevatorCallRequest.getFromFloor();
		boolean riderWantsToGoUp = elevatorCallRequest.wantsToGoUp();

		boolean destinationFloorIsAbove = destinationFloor > currentFloor;

		switch (currentElevatorState)
		{
			case waitingForPassengers:
				if (floorsToStopOn.isEmpty())
					return destinationFloorIsAbove ? destinationFloor - currentFloor : currentFloor - destinationFloor;

				if (elevatorWantsToGoUp())
				{
					if (!destinationFloorIsAbove || !riderWantsToGoUp)
						return null;
					return destinationFloor - currentFloor;
				}
				else
				{
					if (destinationFloorIsAbove || riderWantsToGoUp)
						return null;
					return currentFloor - destinationFloor;
				}
			case waitingForService:
				return null;
			case movingUpWhileEmpty:
			case movingUpWhileOccupied:
			{
				if (!destinationFloorIsAbove || !riderWantsToGoUp)
					return null;
				return destinationFloor - currentFloor;
			}
			case movingDownWhileEmpty:
			case movingDownWhileOccupied:
			{
				if (destinationFloorIsAbove || riderWantsToGoUp)
					return null;
				return currentFloor - destinationFloor;
			}
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + currentElevatorState);
		}
	}

	// Elevators can only move during a tick event.
	public void tick()
	{
		switch (currentElevatorState)
		{
			case waitingForPassengers:
				boolean elevatorNeedsToMove = !floorsToStopOn.isEmpty();
				if (elevatorNeedsToMove)
				{
					// It will actually move next tick. This tick just updates the state.
					if (elevatorWantsToGoUp())
						changeState(internalRequestReceivedSinceLastTick ? movingUpWhileOccupied : movingUpWhileEmpty);
					else
						changeState(internalRequestReceivedSinceLastTick ? movingUpWhileOccupied : movingUpWhileEmpty);
				}
				else if (numTripsSinceLastService >= MAX_TRIPS_BEFORE_SERVICE)
					changeState(waitingForService);
				break;
			case waitingForService:
				break;
			case movingUpWhileEmpty:
			case movingUpWhileOccupied:
				if (floorsToStopOn.contains(currentFloor))
				{
					floorsToStopOn.remove(currentFloor);
					changeState(waitingForPassengers);
				}
				else
					move(true);
				break;
			case movingDownWhileEmpty:
			case movingDownWhileOccupied:
				if (floorsToStopOn.contains(currentFloor))
				{
					floorsToStopOn.remove(currentFloor);
					changeState(waitingForPassengers);
				}
				else
					move(false);
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + currentElevatorState);
		}

		// Blank this out at the end of each tick.
		internalRequestReceivedSinceLastTick = false;

		monitor.report("Elevator state " + currentElevatorState, elevatorNumber, currentFloor);
	}

	public void finalReport()
	{
		monitor.report(
				"Final report:"
						+ " numTripsSinceLastService=" + numTripsSinceLastService
						+ " totalNumTrips=" + totalNumTrips
						+ " totalFloorsPassed=" + totalFloorsPassed,
				elevatorNumber);
	}
}
