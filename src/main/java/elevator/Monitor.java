package elevator;

// todo: Can make this a singleton (easy when using Spring)
public class Monitor
{
	public void report(
			String message,
			int elevatorNumber,
			int floorNumber)
	{
		System.out.println("Elevator " + elevatorNumber + " on floor " + floorNumber + ": " + message);
	}
}
