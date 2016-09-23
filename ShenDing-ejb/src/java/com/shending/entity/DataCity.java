/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "data_city")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DataCity.findAll", query = "SELECT d FROM DataCity d"),
    @NamedQuery(name = "DataCity.findById", query = "SELECT d FROM DataCity d WHERE d.id = :id"),
    @NamedQuery(name = "DataCity.findByName", query = "SELECT d FROM DataCity d WHERE d.name = :name"),
    @NamedQuery(name = "DataCity.findBySortIndex", query = "SELECT d FROM DataCity d WHERE d.sortIndex = :sortIndex"),
    @NamedQuery(name = "DataCity.findByCode", query = "SELECT d FROM DataCity d WHERE d.code = :code"),
    @NamedQuery(name = "DataCity.findByProvinceCode", query = "SELECT d FROM DataCity d WHERE d.provinceCode = :provinceCode"),
    @NamedQuery(name = "DataCity.findByPinyin", query = "SELECT d FROM DataCity d WHERE d.pinyin = :pinyin")})
public class DataCity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sort_index")
    private int sortIndex;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "code")
    private String code;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "province_code")
    private String provinceCode;
    @Size(max = 255)
    @Column(name = "pinyin")
    private String pinyin;

    public DataCity() {
    }

    public DataCity(Long id) {
        this.id = id;
    }

    public DataCity(Long id, int sortIndex, String code, String provinceCode) {
        this.id = id;
        this.sortIndex = sortIndex;
        this.code = code;
        this.provinceCode = provinceCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
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
        if (!(object instanceof DataCity)) {
            return false;
        }
        DataCity other = (DataCity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.shending.entity.DataCity[ id=" + id + " ]";
    }
    
}
