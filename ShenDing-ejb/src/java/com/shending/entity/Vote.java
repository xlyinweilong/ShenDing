package com.shending.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 投票
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "vote")
@XmlRootElement
public class Vote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "q1")
    private String q1;
    @Size(max = 255)
    @Column(name = "q2")
    private String q2;
    @Size(max = 255)
    @Column(name = "q3")
    private String q3;
    @Size(max = 255)
    @Column(name = "q4")
    private String q4;
    @Size(max = 255)
    @Column(name = "q5")
    private String q5;
    @Size(max = 255)
    @Column(name = "q6")
    private String q6;
    @Size(max = 255)
    @Column(name = "q7")
    private String q7;
    @Size(max = 255)
    @Column(name = "q8")
    private String q8;
    @Size(max = 255)
    @Column(name = "q9")
    private String q9;
    @Size(max = 255)
    @Column(name = "q10")
    private String q10;
    @Size(max = 255)
    @Column(name = "q11")
    private String q11;
    @Size(max = 255)
    @Column(name = "q12")
    private String q12;
    @Size(max = 255)
    @Column(name = "q13")
    private String q13;
    @Size(max = 255)
    @Column(name = "q14")
    private String q14;
    @Size(max = 255)
    @Column(name = "q15")
    private String q15;
    @Size(max = 255)
    @Column(name = "q16")
    private String q16;

    public Vote() {
    }

    public Vote(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQ1() {
        return q1;
    }

    public void setQ1(String q1) {
        this.q1 = q1;
    }

    public String getQ2() {
        return q2;
    }

    public void setQ2(String q2) {
        this.q2 = q2;
    }

    public String getQ3() {
        return q3;
    }

    public void setQ3(String q3) {
        this.q3 = q3;
    }

    public String getQ4() {
        return q4;
    }

    public void setQ4(String q4) {
        this.q4 = q4;
    }

    public String getQ5() {
        return q5;
    }

    public void setQ5(String q5) {
        this.q5 = q5;
    }

    public String getQ6() {
        return q6;
    }

    public void setQ6(String q6) {
        this.q6 = q6;
    }

    public String getQ7() {
        return q7;
    }

    public void setQ7(String q7) {
        this.q7 = q7;
    }

    public String getQ8() {
        return q8;
    }

    public void setQ8(String q8) {
        this.q8 = q8;
    }

    public String getQ9() {
        return q9;
    }

    public void setQ9(String q9) {
        this.q9 = q9;
    }

    public String getQ10() {
        return q10;
    }

    public void setQ10(String q10) {
        this.q10 = q10;
    }

    public String getQ11() {
        return q11;
    }

    public void setQ11(String q11) {
        this.q11 = q11;
    }

    public String getQ12() {
        return q12;
    }

    public void setQ12(String q12) {
        this.q12 = q12;
    }

    public String getQ13() {
        return q13;
    }

    public void setQ13(String q13) {
        this.q13 = q13;
    }

    public String getQ14() {
        return q14;
    }

    public void setQ14(String q14) {
        this.q14 = q14;
    }

    public String getQ15() {
        return q15;
    }

    public void setQ15(String q15) {
        this.q15 = q15;
    }

    public String getQ16() {
        return q16;
    }

    public void setQ16(String q16) {
        this.q16 = q16;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vote)) {
            return false;
        }
        Vote other = (Vote) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.shending.entity.Vote[ id=" + id + " ]";
    }

}
