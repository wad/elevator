package elevator;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ElevatorController
{
	int timeCounter = 0;
	Monitor monitor = new Monitor();

	List<Elevator> elevators;
	List<ElevatorCallRequest> pendingRequests = new ArrayList<>();

	public ElevatorController(
			int numElevators,
			int numFloors)
	{
		if (numElevators < 1)
			throw new IllegalArgumentException("Need at least one elevator.");

		if (numFloors < 2)
			throw new IllegalArgumentException("Need at least two floors.");

		elevators = new ArrayList<>(numElevators);
		for (int i = 0; i < numElevators; i++)
			elevators.add(new Elevator(monitor, numFloors, i));

		monitor.report("Elevator controller started with " + numElevators + " elevators and " + numFloors + " floors");
	}

	public void tick()
	{
		monitor.report("tick " + timeCounter);
		handleRequests();
		elevators.forEach(Elevator::tick);
		timeCounter++;
	}

	void handleRequests()
	{
		pendingRequests = pendingRequests
				.stream()
				.filter(request -> !handleRequest(request))
				.collect(toList());
	}

	public void addRequest(ElevatorCallRequest elevatorCallRequest)
	{
		monitor.report("Adding request: Person on floor " + elevatorCallRequest.getFromFloor() + " pushed the " + (elevatorCallRequest.wantsToGoUp() ? "up" : "down") + " button");
		pendingRequests.add(elevatorCallRequest);
	}

	// return true if the request was handled
	boolean handleRequest(ElevatorCallRequest elevatorCallRequest)
	{
		monitor.report("Handling request: Person on floor " + elevatorCallRequest.getFromFloor() + " pushed the " + (elevatorCallRequest.wantsToGoUp() ? "up" : "down") + " button");

		int smallestNumFloorsAway = Integer.MAX_VALUE;
		Integer selectedElevatorNumber = null;
		for (Elevator elevator : elevators)
		{
			Integer numFloorsToMove = elevator.howManyFloorsAreYouFromBeingAbleToStopHere(elevatorCallRequest);
			if (numFloorsToMove != null && numFloorsToMove < smallestNumFloorsAway)
				selectedElevatorNumber = elevator.elevatorNumber;
		}

		if (selectedElevatorNumber == null)
		{
			monitor.report("No elevators can respond to call request. Will try again next tick.");
			return false;
		}
		else
		{
			monitor.report("Elevator " + selectedElevatorNumber + " was selected to receive call request.");
			elevators.get(selectedElevatorNumber).externalFloorRequest(elevatorCallRequest.getFromFloor());
			return true;
		}
	}

	public void riderRequest(
			int elevatorNumber,
			int toFloor)
	{
		monitor.report("Rider in elevator " + elevatorNumber + " pushed floor button " + toFloor);
		elevators.get(elevatorNumber).internalFloorRequest(toFloor);
	}

	public void markServiceIsComplete(int elevatorNumber)
	{
		monitor.report("Service performed", elevatorNumber);
		elevators.get(elevatorNumber).serviceIsComplete();
	}
}
