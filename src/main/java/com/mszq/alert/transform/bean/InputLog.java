package com.mszq.alert.transform.bean;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InputLog {
    private double date;
    private String log;
    private String time;
    private KubernetesInfo kubernetes;

    private static final String ALERT_TYPE = "logging";
    private static final String CUSTOM_LABEL = "日志告警";
    private static final String WORKSPACE = "mszq";
    private static final String RECEIVER = "global-webhook-receiver";
    private static final String DEFAULT_NODE = "worker-s001";
    private static final String STATUS = "firing";
    private static final String DEFAULT_TIME = "0001-01-01T00:00:00Z";
    private static final boolean NOTIFY_SUCCESSFUL = false;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    static class KubernetesInfo {
        String podName;
        String namespaceName;
        String containerName;
        String dockerId;
        String containerImage;
    }

    public AlertOutput toAlertOutput(String cluster,String severity) {
        AlertOutput.AlertOutputBuilder builder = AlertOutput.builder();

        String id = String.valueOf(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);

        List<AlertOutput.Alert> alerts = new ArrayList<>();
        AlertOutput.Labels labels = new AlertOutput.Labels();
        labels.alerttype = ALERT_TYPE;
        labels.cluster = cluster;
        labels.customLabel = CUSTOM_LABEL;
        labels.severity = severity;
        labels.alertname =String.format("%s-%s-%s",cluster,this.kubernetes.namespaceName,this.kubernetes.containerName);
        labels.workspace = WORKSPACE;
        labels.receiver = RECEIVER;
        labels.pod = this.kubernetes.podName;
        labels.timestamp = this.time;
        labels.container = this.kubernetes.containerName;
        labels.namespace = this.kubernetes.namespaceName;
        labels.node = DEFAULT_NODE;

        AlertOutput.Annotations annotations = new AlertOutput.Annotations();
        annotations.log = this.log;
        annotations.message = this.log;
        annotations.summary = labels.alertname;
        annotations.summaryCn = String.format("集群: %s, namespace: %s, Pod: %s, 容器: %s 发生异常日志告警",cluster,this.kubernetes.namespaceName,this.kubernetes.podName,this.kubernetes.containerName);
        annotations.time = this.time;

        AlertOutput.Alert alert = new AlertOutput.Alert();
        alert.id = id;
        alert.status = STATUS;
        alert.labels = labels;
        alert.annotations = annotations;
        alert.startsAt = this.time;
        alert.endsAt = this.time;
        alert.NotifySuccessful = NOTIFY_SUCCESSFUL;

        alerts.add(alert);

        builder.alerts(alerts);
        builder.groupLabels(labels.groupLabelSubset());
        builder.commonLabels(labels);
        builder.commonAnnotations(annotations);
        return builder.build();
    }
}
