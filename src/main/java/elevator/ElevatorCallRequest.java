package elevator;

public class ElevatorCallRequest
{
	private int fromFloor;
	private boolean wantsToGoUp;

	public ElevatorCallRequest(int fromFloor, boolean wantsToGoUp)
	{
		this.fromFloor = fromFloor;
		this.wantsToGoUp = wantsToGoUp;
	}

	public int getFromFloor()
	{
		return fromFloor;
	}

	public boolean wantsToGoUp()
	{
		return wantsToGoUp;
	}
}
