package elevator;

public enum ElevatorState
{
	waitingForPassengers(true, false),
	waitingForService(true, false),
	movingUpWhileEmpty(false, false),
	movingDownWhileEmpty(false, false),
	movingUpWhileOccupied(false, true),
	movingDownWhileOccupied(false, true);

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
