package com.lineage.server.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * org.w3c.dom.NodeListにIterableを付加するためのアダプタ。
 */
// 標準ライブラリに同じものが用意されているようなら置換してください。
public class IterableNodeList implements Iterable<Node> {
	private final NodeList _list;

	private class MyIterator implements Iterator<Node> {
		private int _idx = 0;

		@Override
		public boolean hasNext() {
			return _idx < _list.getLength();
		}

		@Override
		public Node next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return _list.item(_idx++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public IterableNodeList(final NodeList list) {
		_list = list;
	}

	@Override
	public Iterator<Node> iterator() {
		return new MyIterator();
	}

}
