package com.mszq.alert.transform.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertOutput {
    List<Alert> alerts;
    Labels groupLabels;
    Labels commonLabels;
    Annotations commonAnnotations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Alert {
        String id;
        String status;
        Labels labels;
        Annotations annotations;
        String startsAt;
        String endsAt;
        boolean NotifySuccessful;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Labels {
        String alerttype;
        String cluster;
        String customLabel;
        String severity;
        String alertname;
        String workspace;
        String receiver;
        String pod;
        String timestamp;
        String container;
        String namespace;
        String node;

        public Labels groupLabelSubset() {
            Labels group = new Labels();
            group.alerttype = this.alerttype;
            group.cluster = this.cluster;
            group.namespace = this.namespace;
            group.severity = this.severity;
            group.alertname = this.alertname;
            return group;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Annotations {
        String log;
        String message;
        String summary;
        String summaryCn;
        String time;
    }
}
