package net.w3e.wlib.json.formula;

import java.util.List;

import net.w3e.wlib.json.formula.nodes.FormulaNode;

public enum FormulaType implements FormulaNode {
	ROOT,
	NODE,

	LEFT_BRACKET {
		@Override
		public String buildString() {
			return "(";
		}
	},
	RIGHT_BRACKET {
		@Override
		public String buildString() {
			return ")";
		}
	},
	COMMA {
		@Override
		public String buildString() {
			return ",";
		}
	},

	NUMBER,
	MAT,
	CAST,
	ARGS,
	METHOD,

	;

	@Override
	public FormulaType getType() {
		return this;
	}

	@Override
	public String command() {
		return buildString();
	}

	public static final List<FormulaType> VALUE_LIST = List.of(NUMBER, ARGS, METHOD);

	/*public boolean isCompoiste() {
		return this == NODE || this == LEFT_BRACKET || this == RIGHT_BRACKET;
	}*/
}
