package server;

public class ReaderManager implements Runnable {

	@Override
	public void run() {
		try {
			String filename = System.getProperty("user.dir")
					+ "/src/server/temp.rss";
			Thread t = new Thread();
			while (true) {
				if (t.isAlive()) {
					Thread.sleep(10000);
				} else {
					t = new Thread(new TwitterReader(filename, 50));
					t.start();
					Thread.sleep(10000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
