import java.util.*;

public class Geometry {
    @interface _n {}
    class NumUtil {
        final @_n double E = 1e-5;
        @_n double diff(@_n double a, @_n double b){ return Math.abs(a-b); }
        boolean eq(@_n double a, @_n double b) { return diff(a, b) <= E; }
        boolean nz(@_n double n) { return diff(n, 0) > E; }
    }
    final NumUtil _N = new NumUtil();
    class Point {
        @_n double x, y;
        Point(@_n double X, @_n double Y) { x = X; y = Y; }
        @_n double distance(Point p) {
            @_n double dx = p.x - x;
            @_n double dy = p.y - y;
            return Math.sqrt(dx*dx + dy*dy);
        }
        boolean inBox(Point p, Point q) {
            return x >= Math.min(p.x, q.x)-_N.E && x <= Math.max(p.x, q.x)+_N.E
                && y >= Math.min(p.y, q.y)-_N.E && y <= Math.max(p.y, q.y)+_N.E;
        }
        public String toString() { return String.format("(%g,%g)", x, y); }
    }
    class Line {
        @_n double a, b, c;  // ax + by + c = 0
        Line(@_n double A, @_n double B, @_n double C) { a=A; b=B; c=C; }
        Line(Point p1, Point p2) {
            if (_N.eq(p1.x, p2.x)) { a = 1; b = 0; c = -p1.x; }
            else { a = (p1.y-p2.y)/(p2.x-p1.x); b = 1; c = -a*p1.x - p1.y; }
        }
        Line(Point p, @_n double m) { a = -m; b = 1; c = m*p.x-p.y; }
        boolean isParallel(Line l) { return _N.eq(a, l.a) && _N.eq(b, l.b); }
        boolean isSame(Line l) { return isParallel(l) && _N.eq(c, l.c); }
        boolean isVertical() { return !_N.nz(b); }
        boolean isHorizontal() { return !_N.nz(a); }
        Point intersection(Line l) {
            if (isParallel(l)) return null;
            @_n double x = (l.b*c - b*l.c) / (l.a*b - a*l.b);
            Line rl = isVertical() ? l : this;
            return new Point(x, -(rl.a * x + rl.c) / rl.b);
        }
        Point closestTo(Point p) {
            if (isVertical()) return new Point(-c, p.y);
            if (isHorizontal()) return new Point(p.x, -c);
            return intersection(new Line(p, 1/a));
        }
    }
    class Circle {
        @_n double x, y, r;
        Circle(@_n double X, @_n double Y, @_n double R) { x=X; y=Y; r=R; }
        Point[] tangentPoints(Point p) {
            @_n double pox = x - p.x;
            @_n double poy = y - p.y;
            @_n double h2 = pox*pox + poy*poy;
            @_n double s = Math.sqrt(h2 - r*r);
            Point[] res = {
                new Point(p.x + s*(pox*s-poy*r)/h2, p.y + s*(poy*s+pox*r)/h2),
                new Point(p.x + s*(pox*s+poy*r)/h2, p.y + s*(poy*s-pox*r)/h2)
            };
            return res;
        }
        public String toString() { return String.format("(%g,%g:%g)", x, y, r); }
    }
    class Segment {
        Point a, b;
        Segment(Point A, Point B) { a=A; b=B; }
        Point intersection(Segment s) {
            Line l1 = new Line(a, b);
            Line l2 = new Line(s.a, s.b);
            if (l1.isParallel(l2)) return null;
            Point in = l1.intersection(l2);
            if (in.inBox(a, b) && in.inBox(s.a, s.b)) return in;
            return null;
        }
        Point raySegmentIntersection(Segment s) {
            Line l1 = new Line(a, b);
            Line l2 = new Line(s.a, s.b);
            if (l1.isParallel(l2)) return null;
            Point in = l1.intersection(l2);
            int dirXa = (int)Math.signum(b.x-a.x);
            int dirYa = (int)Math.signum(b.y-a.y);
            int dirXb = (int)Math.signum(a.x-in.x);
            int dirYb = (int)Math.signum(a.y-in.y);
            if (dirXa == -dirXb && dirYa == -dirYb
                && in.inBox(s.a, s.b)) return in;
            return null;
        }
        public String toString() { return String.format("%s-%s", a, b); }
    }
    class Interval {
        @_n double a, b;
        Interval(@_n double A, @_n double B) {
            if (A > B) { a=B; b=A; }
            else { a=A; b=B; }
        }
        boolean contains(@_n double v) {
            return v >= a && v <= b;
        }
        @_n double length() { return b-a; }
        Interval intersection(Interval i2) {
            if (a > i2.b || i2.a > b) return null;
            return new Interval(Math.max(a, i2.a), Math.min(b, i2.b));
        }
        public String toString() { return String.format("[%g:%g]", a, b); }
    }
    final Comparator<Interval> cmpInterval = new Comparator<Interval>() {
        public int compare(Interval a, Interval b) {
            return a.a<b.a ? -1 : (a.a>b.a ? 1 : 0);
        }
    };
    class IntervalGroup {
        @_n double inf, sup;
        boolean sorted;
        List<Interval> is;
        IntervalGroup(@_n double Inf, @_n double Sup) {
            inf=Inf; sup=Sup; sorted = true;
            is = new ArrayList<Interval>();
        }
        void add(Interval i) {
            if (i.a < inf) i.a = inf;
            if (i.b > sup) i.b = sup;
            if (sorted) sorted = false;
            is.add(i);
        }
        void normalise() {
            if (is.size() < 2) return;
            sort();
            List<Interval> nl = new ArrayList<Interval>();
            Interval aux = is.get(0);
            for (Interval in : is.subList(1, is.size())) {
                if (aux.contains(in.a)) aux.b = Math.max(aux.b, in.b);
                else { nl.add(aux); aux = in; }
            }
            nl.add(aux);
            is = nl;
        }
        IntervalGroup intersect(IntervalGroup g) {
            IntervalGroup ng = new IntervalGroup(inf, sup);
            if (is.size() < 1 || g.is.size() < 1) return ng;
            sort(); g.sort();
            ListIterator<Interval> it1 = is.listIterator();
            ListIterator<Interval> it2 = g.is.listIterator();
            Interval p = it1.next();
            Interval q = it2.next();
            while (p != null && q != null) {
                Interval in = p.intersection(q);
                if (in != null) ng.add(in);
                if (p.b < q.b) p = it1.hasNext() ? it1.next() : null;
                else q = it2.hasNext() ? it2.next() : null;
            }
            ng.sorted = true;
            return ng;
        }
        @_n double length() {
            if (is.size() < 1) return 0;
            sort();
            @_n double res = 0;
            Interval aux = is.get(0);
            for (Interval in : is.subList(1, is.size())) {
                if (aux.contains(in.a)) aux.b = Math.max(aux.b, in.b);
                else { res += aux.length(); aux = in; }
            }
            return res + aux.length();
        }
        void sort() {
            if (sorted) return;
            Collections.sort(is, cmpInterval);
            sorted = true;
        }
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            for (Interval i : is) {
                sb.append(String.format(" %s", i));
            }
            return sb.toString();
        }
    }
}
