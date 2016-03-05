package elevator;

public enum ElevatorState
{
	waitingForPassengers(true, false),
	waitingForService(true, false),
	movingWhileEmpty(false, false),
	movingPassengers(false, true);

	boolean doorsAreOpen;
	boolean isOccupied;

	ElevatorState(
			boolean doorsAreOpen,
			boolean isOccupied)
	{
		this.doorsAreOpen = doorsAreOpen;
		this.isOccupied = isOccupied;
	}
}
