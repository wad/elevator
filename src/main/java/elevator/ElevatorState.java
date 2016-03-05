package elevator;

public enum ElevatorState
{
	waitingForPassengers(true, false, false, false),
	waitingForService(true, false, false, false),
	movingUpWhileEmpty(false, false, true, false),
	movingDownWhileEmpty(false, false, false, true),
	movingUpWhileOccupied(false, true, true, false),
	movingDownWhileOccupied(false, true, false, true);

	boolean doorsAreOpen;
	boolean isOccupied;
	boolean isGoingUp;
	boolean isGoingDown;

	ElevatorState(
			boolean doorsAreOpen,
			boolean isOccupied,
			boolean isGoingUp,
			boolean isGoingDown)
	{
		this.doorsAreOpen = doorsAreOpen;
		this.isOccupied = isOccupied;
		this.isGoingUp = isGoingUp;
		this.isGoingDown = isGoingDown;
	}
}
