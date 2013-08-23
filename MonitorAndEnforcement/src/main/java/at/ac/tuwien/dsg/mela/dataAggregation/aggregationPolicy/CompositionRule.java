package at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy;

import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Metric;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.MetricValue;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;

import com.google.common.util.concurrent.UncheckedExecutionException;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: daniel-tuwien Date: 1/30/13 Time: 9:47 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CompositionRule")
public class CompositionRule {
	@XmlElement(name = "TargetMetric", required = true)
	private Metric targetMetric;

	@XmlElement(name = "ResultingMetric", required = true)
	private Metric resultingMetric;

	@XmlAttribute(name = "SourceServiceElementLevel", required = true)
	private ServiceElement.ServiceElementLevel metricSourceServiceElementLevel;

	@XmlElement(name = "SourceServiceElementID", required = false)
	private Collection<String> metricSourceServiceElementIDs;

	@XmlElement(name = "Operation", required = true)
	private Collection<CompositionOperation> operations;

{
        operations = new ArrayList<CompositionOperation>();
    }

	public CompositionRule() {

		this.operations = new ArrayList<CompositionOperation>();
	}

	public Metric getTargetMetric() {
		return targetMetric;
	}

	public ServiceElement.ServiceElementLevel getMetricSourceServiceElementLevel() {
		return metricSourceServiceElementLevel;
	}

	public void setMetricSourceServiceElementLevel(
			ServiceElement.ServiceElementLevel metricSourceServiceElementLevel) {
		this.metricSourceServiceElementLevel = metricSourceServiceElementLevel;
	}

	public Collection<String> getMetricSourceServiceElementIDs() {
		return metricSourceServiceElementIDs;
	}

	public void setMetricSourceServiceElementIDs(
			Collection<String> metricSourceServiceElementIDs) {
		this.metricSourceServiceElementIDs = metricSourceServiceElementIDs;
	}

	public void setTargetMetric(Metric targetMetric) {
		this.targetMetric = targetMetric;
	}

	public Metric getResultingMetric() {
		return resultingMetric;
	}

	public void setResultingMetric(Metric resultingMetric) {
		this.resultingMetric = resultingMetric;
	}

	public Collection<CompositionOperation> getOperations() {
		return operations;
	}

	public void setOperations(Collection<CompositionOperation> operations) {
		this.operations = operations;
	}

 public void addOperation(CompositionOperation  operation) {
        this.operations.add(operation);
    }

@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositionRule that = (CompositionRule) o;

        if (metricSourceServiceElementIDs != null ? !metricSourceServiceElementIDs.equals(that.metricSourceServiceElementIDs) : that.metricSourceServiceElementIDs != null)
            return false;
        if (metricSourceServiceElementLevel != that.metricSourceServiceElementLevel) return false;
        if (operations != null ? !operations.equals(that.operations) : that.operations != null) return false;
        if (resultingMetric != null ? !resultingMetric.equals(that.resultingMetric) : that.resultingMetric != null)
            return false;
        if (targetMetric != null ? !targetMetric.equals(that.targetMetric) : that.targetMetric != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetMetric != null ? targetMetric.hashCode() : 0;
        result = 31 * result + (resultingMetric != null ? resultingMetric.hashCode() : 0);
        result = 31 * result + (metricSourceServiceElementLevel != null ? metricSourceServiceElementLevel.hashCode() : 0);
        result = 31 * result + (metricSourceServiceElementIDs != null ? metricSourceServiceElementIDs.hashCode() : 0);
        result = 31 * result + (operations != null ? operations.hashCode() : 0);
        return result;
    }

	/**
	 * 
	 * @param values
	 *            list of values to be composed
	 * @return
	 */
	public MetricValue apply(List<MetricValue> values) {

		// operations done on result
		// for example SUM will place the sum on result[0]
		// method returns result[0]
		List<MetricValue> result = new ArrayList<MetricValue>();

		for (MetricValue value : values) {
			result.add(value.clone());
		}

		for (CompositionOperation operation : this.getOperations()) {
			Double operator = 0.0d;
			String value = operation.getValue();
			if (value != null && value.length() > 0) {
				operator = Double.parseDouble(value);
			}

			switch (operation.getOperation()) {
			case ADD:
				for (MetricValue metricValue : result) {
					if (metricValue.getValueType() == MetricValue.ValueType.NUMERIC) {
						metricValue.setValue(((Number) metricValue.getValue())
								.doubleValue() + operator);
					}
				}
				break;
			case AVG: {
				Double avg = 0.0d;
				for (MetricValue metricValue : result) {
					if (metricValue.getValueType() == MetricValue.ValueType.NUMERIC) {
						avg += (((Number) metricValue.getValue()).doubleValue());
					}
				}
				MetricValue metricValue = new MetricValue();
				metricValue.setValue(avg / result.size());
				result.clear();
				result.add(metricValue);
			}
				break;
			case CONCAT: {
				String concat = "";
				for (MetricValue metricValue : result) {
					concat += metricValue.getValue().toString() + ",";
				}
				MetricValue metricValue = new MetricValue();
				metricValue.setValue(concat);
				result.clear();
				result.add(metricValue);
			}
				break;
			case DIV:
				for (MetricValue metricValue : result) {
					if (metricValue.getValueType() == MetricValue.ValueType.NUMERIC) {
						if (operator != 0) {
							metricValue.setValue((((Number) metricValue
									.getValue()).doubleValue()) / operator);
						} else {
							metricValue.setValue((((Number) metricValue
									.getValue()).doubleValue()));
						}
					}
				}
				break;
			case KEEP:
				break;
			case MAX: {
				Double max = 0.0d;
				for (MetricValue metricValue : result) {
					if (metricValue.getValueType() == MetricValue.ValueType.NUMERIC) {
						if (max < (((Number) metricValue.getValue())
								.doubleValue())) {
							max = (((Number) metricValue.getValue())
									.doubleValue());
						}
					}
				}
				MetricValue metricValue = new MetricValue();
				metricValue.setValue(max);
				result.clear();
				result.add(metricValue);
			}
				break;
			case MIN: {
				Double min = 0.0d;
				for (MetricValue metricValue : result) {
					if (metricValue.getValueType() == MetricValue.ValueType.NUMERIC) {
						if (min > (((Number) metricValue.getValue())
								.doubleValue())) {
							min = (((Number) metricValue.getValue())
									.doubleValue());
						}
					}
				}
				MetricValue metricValue = new MetricValue();
				metricValue.setValue(min);
				result.clear();
				result.add(metricValue);
			}
				break;
			case MUL:
				for (MetricValue metricValue : result) {
					if (operator != 0) {
						metricValue.setValue((((Number) metricValue.getValue())
								.doubleValue()) * operator);
					} else {
						metricValue.setValue((((Number) metricValue.getValue())
								.doubleValue()));
					}
				}
				break;
			case SUB:
				for (MetricValue metricValue : result) {
					if (metricValue.getValueType() == MetricValue.ValueType.NUMERIC) {
						metricValue.setValue((((Number) metricValue.getValue())
								.doubleValue()) - operator);
					}
				}
				break;
			case SUM:
				Double sum = 0.0d;
				for (MetricValue metricValue : result) {
					if (metricValue.getValueType() == MetricValue.ValueType.NUMERIC) {
						sum += (((Number) metricValue.getValue()).doubleValue());
					}
				}
				MetricValue metricValue = new MetricValue();
				metricValue.setValue(sum);
				result.clear();
				result.add(metricValue);
				break;
			case UNION:
				break;
			case KEEP_LAST:
				MetricValue last = result.get(result.size() - 1);
				result.clear();
				result.add(last);
				break;
			case KEEP_FIRST:
				MetricValue first = result.get(0);
				result.clear();
				result.add(first);
				break;
			case SET_VALUE:
			{
				MetricValue newVal = new MetricValue();
				newVal.setValue(operator);
				result.clear();
				result.add(newVal);
			}
				break;

			}
		}

		return result.get(0);
	}



}
