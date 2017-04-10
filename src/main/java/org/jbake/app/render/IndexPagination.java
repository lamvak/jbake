package org.jbake.app.render;

public enum IndexPagination {
	PAGINATE {
		@Override
		public boolean shouldBeApplied() {
			return true;
		}
	},
	NON_PAGINATE;

	public boolean shouldBeApplied() {
		return false;
	}
}
