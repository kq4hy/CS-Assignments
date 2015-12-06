import java.util.Random;

public class testing {

	public testing() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Random rn = new Random();
		for (int i = 0; i < 20; i ++) {
			System.out.println(rn.nextInt(2 - 1 + 1 ) + 1);
		}
	}

}
