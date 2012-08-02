%define _metlog_dir /opt/metlog

Name:          metlog-hive
Version:       0.1.0
Release:       1
Summary:       Apache Hive extensions to support Metlog
Packager:      Victor Ng <vng@mozilla.com>
Group:         Development/Libraries
License:       MPL 2.0
URL:           https://github.com/mozilla-services/metlog-hive
Source0:       %{name}.tar.gz
BuildRoot:     %(mktemp -ud %{_tmppath}/%{name}-%{version}-%{release}-XXXXXX)
AutoReqProv:   no
Requires:      hadoop-hive >= 0.7.1

%description
Apache Hive extensions for better querying capability.  Currently, we provide
a more powerful replacement for the json_tuple UDTF.

%prep
%setup -q -c -n metlog_hive

%build

%install
rm -rf %{buildroot}
mkdir -p %{buildroot}%{_metlog_dir}/hadoop/hive
cp -rp dist/lib/MetlogHive*jar %{buildroot}%{_metlog_dir}/hadoop/hive

%clean
rm -rf %{buildroot}

%files
%defattr(-,root,root,-)
%{_metlog_dir}/hadoop/hive

%changelog
* Tue Aug 2 2012 Victor Ng <vng@mozilla.com>
- Initial package
