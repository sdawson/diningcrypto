package client;

import java.util.ArrayList;

import communication.Key;
import communication.Keyop;
import communication.Message;

public class TestAddition {
	/* Assumes only capital A - Z are used in messages. */
	private static final int base = (int) 'A';

	public static void main(String[] args) {
		char tryChar = (char) 'B' - base;
		char test = (char) ('A' - tryChar);
		char another = (char) ('A' + 1);
		char a2 = (char) ('A' + ((int) 'B'));
		
		System.out.println((int) another + " " +  another);
		System.out.println("offset test " + test);
		System.out.println("offset test 2 " + (char)(base + 25));
		
		ArrayList<Key> y = new ArrayList<Key>();
		y.add(new Key('B', Keyop.ADD));
		y.add(new Key('D', Keyop.SUBTRACT));
		ArrayList<Key> x = new ArrayList<Key>();
		x.add(new Key('B', Keyop.SUBTRACT));
		x.add(new Key('C', Keyop.ADD));
		ArrayList<Key> z = new ArrayList<Key>();
		z.add(new Key('C', Keyop.SUBTRACT));
		z.add(new Key('D', Keyop.ADD));
		Message xm = new Message('@', x);
		Message ym = new Message('A', y);
		Message zm = new Message('@', z);
		System.out.println("original messages = " + xm.getIndMessage()
				+ " " + ym.getIndMessage() + " " + zm.getIndMessage());
		xm.encode();
		ym.encode();
		zm.encode();
		ArrayList<Character> finalarray = new ArrayList<Character>();
		finalarray.add(new Character(ym.getIndMessage()));
		finalarray.add(new Character(zm.getIndMessage()));
		System.out.println("final messages = " + xm.getIndMessage()
				+ " " + ym.getIndMessage() + " " + zm.getIndMessage());
		System.out.println("encoded message = " + xm.combineMessages(finalarray));
//		ArrayList<Character> finalarray = new ArrayList<Character>();
//		finalarray.add(new Character('Y'));
//		finalarray.add(new Character('A'));
//		Message jjj = new Message('A', null);
//		System.out.println("orig " + jjj.getIndMessage());
//		System.out.println("final " + jjj.combineMessages(finalarray));
	}

}
