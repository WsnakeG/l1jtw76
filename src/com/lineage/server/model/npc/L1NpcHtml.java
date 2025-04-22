package com.lineage.server.model.npc;

public class L1NpcHtml {
	private final String _name;
	private final String _args[];

	public static final L1NpcHtml HTML_CLOSE = new L1NpcHtml("");

	public L1NpcHtml(final String name) {
		this(name, new String[] {});
	}

	public L1NpcHtml(final String name, final String... args) {
		if ((name == null) || (args == null)) {
			throw new NullPointerException();
		}
		_name = name;
		_args = args;
	}

	public String getName() {
		return _name;
	}

	public String[] getArgs() {
		return _args;
	}
}
