

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;


@XmlRootElement(name="CostCenters")
@JsonRootName("CostCenters")
public class CostCenters {
	private List<CostCenter> costCenterList = new ArrayList<CostCenter>();

	@XmlElement(name="CostCenter")
	@JsonProperty("CostCenter")
	public List<CostCenter> getCostCenterList() {
		return costCenterList;
	}

	public void setCostCenterList(List<CostCenter> costCenterList) {
		this.costCenterList = costCenterList;
	}
}
