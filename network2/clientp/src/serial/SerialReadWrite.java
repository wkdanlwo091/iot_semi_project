package serial;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
public class SerialReadWrite implements SerialPortEventListener {// 어떤 이벤트(데이터 오면) 가 오면 처리한다. 자바 어플리케이션 까지 이벤트가 전이 된다.
	CommPortIdentifier commPortIdentifier;
	CommPort comPort;
	InputStream in;
	BufferedInputStream bin;
	OutputStream out;
	public SerialReadWrite() {
	}
	public SerialReadWrite(String portName) throws NoSuchPortException, UnsupportedCommOperationException, IOException,
			TooManyListenersException, PortInUseException {
		commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		System.out.println("Identifier Com Port!!");
		connect();
		System.out.println("Connect Com Port");
		new Thread(new SerialWriter()).start();
		System.out.println("Start Can Network..");
	}

//제품의 스펙이 다를 때 속도가 다를 떄 ex) bps 가 다를 떄 소프트웨어로 제어한다.
	private void connect()
			throws PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException {
		if (commPortIdentifier.isCurrentlyOwned()) {
			System.out.println("Port is currently ");
		} else {
			try {
				comPort = commPortIdentifier.open(this.getClass().getName(), 5000);// 5초 동안 응답없으면 exception
				if (comPort instanceof SerialPort) {// 시리얼 포트 일때만 할 수 있게
					SerialPort serialPort = (SerialPort) comPort;
					serialPort.addEventListener(this);
					serialPort.notifyOnDataAvailable(true);
					serialPort.setSerialPortParams(921600, // 통신속도
							SerialPort.DATABITS_8, // 데이터 비트
							SerialPort.STOPBITS_1, // stop 비트
							SerialPort.PARITY_NONE); // 패리티
					in = serialPort.getInputStream();
					bin = new BufferedInputStream(in);// bin을 가지고 읽을 때 편하다. inputstream만 있으면 while 문 돌려야 한다.
														// bufferedInputStream은 그냥 읽으면 된다.
					out = serialPort.getOutputStream();
				} else {
					System.out.println("this port is not a serial port");
				}
			} catch (PortInUseException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void serialEvent(SerialPortEvent event) {// 이벤트가 발생하면 이것이 받는다.
//can으로 이벤트가 들어오면 처리한다. 
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[128];

			try {
				while (bin.available() > 0) {
					int numBytes = bin.read(readBuffer);
				}

				String ss = new String(readBuffer);
				System.out.println("Receive Low Data:" + ss + "||");

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

	}

	public void send(String msg) {
		new Thread(new SerialWriter(msg)).start();
	}

	class SerialWriter implements Runnable {// 자바에서 can 통신에 참여할거다.
		String data;/// 캔은 모든 것을 스트링으로 주고 받는다.

		public SerialWriter() {
//나 참여 할거야 메시지를 보내줘야 한다.
			this.data = ":G11A9\r"; // can 프로토콜이라고 한다. 프로토콜은 통신은 아니고 규약 \
//:가 시작 의미 G는 명령코드 11은 체크섬  \r은 끝마침 표시 
//G11A9의 의미는 s
		}

		public SerialWriter(String msg) {
			this.data = convertData(msg);
		}

		public String convertData(String msg) {
//can은 대소문자 구문 안한다. 
//쓰기는 W28 
			msg = msg.toUpperCase();
			msg = "W28" + msg;
//W28 00000000 0000000000000000

			char c[] = msg.toCharArray();// w부터 0까지 문자 배열에 모두 넣었다.
			int checkSum = 0;
			for (char ch : c) {
//모든 캐릭터를 더한다. 
				checkSum += ch;
			}
			checkSum = checkSum & 0xff;// 체크섬을 구하는 비트 연산자

			String result = ":";
			result += msg + Integer.toHexString(checkSum).toUpperCase() + "\r"; // 16진수를 10진수로 바꾸기
//숫자로 나온 체크섬 데이터를 헥사데시멀로 바꾸고 upperCase로 바꾸어 주었다. 
			return result;
		}

		@Override
		public void run() {
			byte[] outData = data.getBytes();
			try {
				out.write(outData);// Can 프로토콜과 맞지 않다.
			} catch (IOException e) {
// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}// 쓰다가 오래걸리거나 쓰다가 멈 출수 있어서 쓰레드로 만들었다.

	public static void main(String[] args) {
		String msg1 = "U28000000000000000000000000";
		char c[] = msg1.toCharArray();

		int checkSum = 0;
		for (char ch : c) {
//모든 캐릭터를 더한다. 
			checkSum += ch;
		}
		checkSum = checkSum & 0xff;// 체크섬을 구하는 비트 연산자
		System.out.println(checkSum);

		System.out.println();
		SerialReadWrite sc = null;
//캔은 대소문자를 모른다. 
		String id = "00000000";
		String data = "0000000000000000";
		String msg = id + data;// 보내는 메시지
//Can은 4자리의 아이디 8자리의 메시지 
		try {
			sc = new SerialReadWrite("COM4");// 해당 포트로 연결
			sc.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}