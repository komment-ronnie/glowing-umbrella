
import React from "react";
import { ResponsivePie } from "@nivo/pie";

const _find = (slices, id) => {
  return slices.findIndex((entry) => entry.id === id);
};

const _contribution = (value, total) => {
  return ((value / total) * 100).toFixed(1);
};


const _coalesce = (slices, value, label) => {
  const idx = _find(slices, "others");
  if (idx === -1) {
    slices.push({
      id: "others",
      label: label,
      value: value,
      contrib: 0,
    });
  } else {
    slices[idx].value += value;
  }
};

const _slices = (
  data,
  keyField,
  valueField,
  unique,
  threshold,
  othersLabel = "Others",
) => {
  const processedSlices =
    data
      ?.reduce((slices, item) => {
        const value = parseInt(item[valueField]);
        if (unique <= 3 || (unique > 4 && value > threshold)) {
          slices.push({
            id: item[keyField],
            label: item[keyField],
            value: value,
            contrib: 0,
          });
        } else {
          _coalesce(slices, value, othersLabel);
        }
        return slices;
      }, [])
      ?.map((slice) => ({
        ...slice,
        contrib: _contribution(
          slice.value,
          data.reduce((total, i) => total + parseInt(i[valueField], 10), 0),
        ),
      })) || [];

  return processedSlices;
};

const Pie = ({
  data,
  amount = data?.length,
  field,
  quantity,
  label,
  threshold = 50,
}) => {
  const unique = new Set(data.map((d) => d[field])).size;
  const datum =
    unique === 0
      ? [{ value: 1, contrib: 100 }]
      : _slices(data, field, quantity, unique, threshold);

  return (
    <div className="pie">
      <ResponsivePie
        data={datum}
        colors={unique > 0 ? ["#26de81"] : ["#5a646a"]}
        margin={{ top: 80, right: 80, bottom: 80, left: 80 }}
        innerRadius={0.97}
        padAngle={2}
        activeOuterRadiusOffset={8}
        borderWidth={1}
        borderColor={{
          from: "color",
          modifiers: [["darker", 0.2]],
        }}
        enableArcLinkLabels={unique > 0}
        enableArcLabels={false}
        // arcLinkLabelsSkipAngle={10}
        arcLinkLabelsTextColor="#eceded"
        arcLinkLabelsThickness={2}
        arcLinkLabelsColor={{ from: "color" }}
        // arcLabelsSkipAngle={10}
        tooltip={(point) => {
          return (
            <div className="pie-tooltip">
              <div className="value">{point?.datum?.data?.contrib} %</div>
            </div>
          );
        }}
        theme={{
          text: {
            fontFamily: "Inter",
            fontSize: "12px",
          },
        }}
      />
      <div className="pie-overlay">
        <div className="amount">{amount?.toLocaleString()}</div>
        <div className="label">{label}</div>
      </div>
    </div>
  );
};

export default Pie;
