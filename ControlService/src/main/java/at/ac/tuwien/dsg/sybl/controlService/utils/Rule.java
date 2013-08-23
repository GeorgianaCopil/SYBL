/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group E184

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package at.ac.tuwien.dsg.sybl.controlService.utils;

public class Rule {
	protected String body;
	protected String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return body;
	}
	public void setText(String text) {
		this.body = text;
	}
	public boolean equals(Object o){
		if (!(o instanceof Rule)){
			return false;
		}
		Rule r = (Rule) o;
		if (r.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())) return true;
		else
		return false;
	}
}
