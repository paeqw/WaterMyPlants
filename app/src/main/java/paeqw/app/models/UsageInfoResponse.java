package paeqw.app.models;

public class UsageInfoResponse {
    private boolean active;
    private CreditLimits credit_limits;
    private Usage used;
    private CanUseCredits can_use_credits;
    private Remaining remaining;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CreditLimits getCredit_limits() {
        return credit_limits;
    }

    public void setCredit_limits(CreditLimits credit_limits) {
        this.credit_limits = credit_limits;
    }

    public Usage getUsed() {
        return used;
    }

    public void setUsed(Usage used) {
        this.used = used;
    }

    public CanUseCredits getCan_use_credits() {
        return can_use_credits;
    }

    public void setCan_use_credits(CanUseCredits can_use_credits) {
        this.can_use_credits = can_use_credits;
    }

    public Remaining getRemaining() {
        return remaining;
    }

    public void setRemaining(Remaining remaining) {
        this.remaining = remaining;
    }

    public static class CreditLimits {
        private Integer day;
        private Integer week;
        private Integer month;
        private Integer total;

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public Integer getWeek() {
            return week;
        }

        public void setWeek(Integer week) {
            this.week = week;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }
    }

    public static class Usage {
        private int day;
        private int week;
        private int month;
        private int total;

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public static class CanUseCredits {
        private boolean value;
        private String reason;

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class Remaining {
        private Integer day;
        private Integer week;
        private Integer month;
        private int total;

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public Integer getWeek() {
            return week;
        }

        public void setWeek(Integer week) {
            this.week = week;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
