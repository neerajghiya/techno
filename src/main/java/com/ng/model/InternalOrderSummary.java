

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@XmlRootElement(name="IOS")
@JsonRootName("IOS")
public class InternalOrderSummary {
	
	private List<InternalOrder> internalOrderList;

	@XmlElement(name="InternalOrder")
	@JsonProperty("InternalOrder")
	public List<InternalOrder> getInternalOrderList() {
		return internalOrderList;
	}

	public void setInternalOrderList(List<InternalOrder> internalOrderList) {
		this.internalOrderList = internalOrderList;
	}
	
}
