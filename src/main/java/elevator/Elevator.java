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

	ElevatorState elevatorState;
	int currentFloor;

	// todo: Refactor this into its own class
	Set<Integer> floorsToStopOn;

	int numTripsSinceLastService = 0;
	int totalNumTrips = 0;
	int totalFloorsPassed = 0;

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
		switch (elevatorState)
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
			boolean shouldDiscardRequest = elevatorWantsToGoUp() != selectedFloorIsAboveCurrentFloor;
			if (shouldDiscardRequest)
				return;
		}

		floorsToStopOn.add(destinationFloor);
	}

	boolean elevatorWantsToGoUp()
	{
		if (floorsToStopOn.isEmpty())
			throw new IllegalStateException("Bug in code! Need to verify there are floors to stop on before calling me.");

		int someFloorToStopOn = (int) floorsToStopOn.toArray()[0];
		return someFloorToStopOn > currentFloor;
	}

	public void serviceIsComplete()
	{
		if (elevatorState == waitingForService)
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
				switch (elevatorState)
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
				switch (elevatorState)
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
				switch (elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						incrementNumTrips();
						openDoors();
						break;
					case movingUpWhileOccupied:
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Bad state change. From " + elevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case movingDownWhileEmpty:
				switch (elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						incrementNumTrips();
						openDoors();
						break;
					case movingDownWhileOccupied:
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
						throw new IllegalStateException("Bad state change. From " + elevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case movingUpWhileOccupied:
			case movingDownWhileOccupied:
				switch (elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						incrementNumTrips();
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

		monitor.report("Changed state from " + elevatorState + " to " + newState, elevatorNumber, currentFloor);

		elevatorState = newState;
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

		switch (elevatorState)
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
				throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
		}
	}

	// Elevators can only move during a tick event.
	public void tick()
	{
		switch (elevatorState)
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
					changeState(waitingForPassengers);
				else
					move(true);
				break;
			case movingDownWhileEmpty:
			case movingDownWhileOccupied:
				if (floorsToStopOn.contains(currentFloor))
					changeState(waitingForPassengers);
				else
					move(false);
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
		}

		// Blank this out at the end of each tick.
		internalRequestReceivedSinceLastTick = false;
	}
}
